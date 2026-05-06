package com.jiniebox.standalone;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

/**
 * 임베디드 Tomcat 11 으로 지니박스를 standalone 으로 실행한다.
 *
 * <p>실행 모드:</p>
 * <ul>
 *   <li><b>개발</b>: {@code src/main/webapp} 디렉토리가 존재하면 그대로 사용
 *       (gradle runStandalone 등)</li>
 *   <li><b>배포</b>: standalone fat jar 안에 임베드된 webapp 리소스를
 *       {@code <dataDir>/webapp/} 로 추출하여 사용</li>
 * </ul>
 *
 * <p>외부 시스템 프로퍼티로 InitializeEnv 와 같은 webapp 코드에
 * standalone 환경 정보를 전달한다:</p>
 * <ul>
 *   <li>{@code jiniebox.standalone=true} — standalone 실행 표시</li>
 *   <li>{@code jiniebox.config.path} — 외부 JINIEBOX.PROPERTIES 절대 경로 (있을 때만)</li>
 *   <li>{@code jiniebox.data.dir} — 데이터 디렉토리 절대 경로</li>
 * </ul>
 */
public class JinieboxStandalone {

    public static void main(String[] args) throws Exception {
        StandaloneOptions opts = StandaloneOptions.parse(args);
        opts.print();

        // 1. 데이터 디렉토리 준비 + 시스템 프로퍼티 설정 (InitializeEnv 가 읽음)
        File dataDir = new File(opts.dataDir).getAbsoluteFile();
        if (!dataDir.exists() && !dataDir.mkdirs()) {
            throw new IOException("데이터 디렉토리를 생성할 수 없습니다: " + dataDir);
        }
        System.setProperty("jiniebox.standalone", "true");
        System.setProperty("jiniebox.data.dir", dataDir.getAbsolutePath());
        if (opts.configPath != null) {
            System.setProperty("jiniebox.config.path",
                    new File(opts.configPath).getAbsoluteFile().getAbsolutePath());
        }

        // 로그 출력 위치를 dataDir/logs 로 (log4j2.xml 의 ${sys:jiniebox.logs.dir} 가 읽음)
        File logsDir = new File(dataDir, "logs");
        if (!logsDir.exists()) logsDir.mkdirs();
        System.setProperty("jiniebox.logs.dir", logsDir.getAbsolutePath());

        // 2. Tomcat 베이스 디렉토리
        File tomcatBase = new File(dataDir, "tomcat-base");
        if (!tomcatBase.exists()) tomcatBase.mkdirs();

        // 3. webapp 위치 결정
        File docBase = resolveDocBase(dataDir);
        System.out.println("[standalone] webapp docBase: " + docBase.getAbsolutePath());

        // 4. Tomcat 부팅
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(tomcatBase.getAbsolutePath());
        tomcat.setPort(opts.port);
        tomcat.getConnector(); // 명시적 연결자 초기화

        Context ctx = tomcat.addWebapp(opts.contextPath, docBase.getAbsolutePath());

        // WAR 가 아닌 일반 디렉토리를 docBase 로 쓸 때, JSP/Jasper 동작을 위해
        // 클래스로더는 부모 위임을 사용하도록 둔다 (기본 동작과 동일).
        ctx.setParentClassLoader(JinieboxStandalone.class.getClassLoader());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                tomcat.stop();
                tomcat.destroy();
            } catch (Exception ignored) { /* shutdown best-effort */ }
        }, "jiniebox-shutdown"));

        tomcat.start();
        System.out.println("[standalone] http://localhost:" + opts.port + opts.contextPath + " 에서 서비스 중");
        tomcat.getServer().await();
    }

    /**
     * docBase 결정:
     * <ol>
     *   <li>{@code -Djiniebox.webapp.dir=...} 가 지정되면 그 디렉토리 (gradle runStandalone 가
     *       WEB-INF/lib 를 뺀 사본을 만들고 가리킨다 — 클래스로더 충돌 방지)</li>
     *   <li>jar 에서 실행 중이면 무조건 jar 안에 임베드된 {@code /webapp/...} 리소스를
     *       {@code dataDir/webapp/} 로 추출하여 사용. cwd 의 {@code src/main/webapp} 는
     *       WEB-INF/lib 가 standalone classpath 와 충돌(log4j 등)하므로 사용하지 않는다.</li>
     *   <li>그 외(클래스 디렉토리에서 직접 실행하는 raw 개발 모드)에 한해 {@code src/main/webapp} 사용</li>
     * </ol>
     */
    private static File resolveDocBase(File dataDir) throws IOException {
        String overrideWebapp = System.getProperty("jiniebox.webapp.dir");
        if (overrideWebapp != null) {
            File f = new File(overrideWebapp);
            if (f.isDirectory()) {
                return f.getAbsoluteFile();
            }
        }

        if (isRunningFromJar()) {
            File extracted = new File(dataDir, "webapp");
            if (!extracted.exists() && !extracted.mkdirs()) {
                throw new IOException("webapp 추출 디렉토리를 만들 수 없습니다: " + extracted);
            }
            extractEmbeddedWebapp(extracted);
            return extracted;
        }

        File devWebapp = new File("src/main/webapp");
        if (devWebapp.isDirectory()) {
            return devWebapp.getAbsoluteFile();
        }

        // 마지막 시도: 클래스 모드인데 src/main/webapp 도 없을 때 → 추출 시도
        File extracted = new File(dataDir, "webapp");
        if (!extracted.exists() && !extracted.mkdirs()) {
            throw new IOException("webapp 디렉토리를 찾을 수 없습니다 (jiniebox.webapp.dir / src/main/webapp / 임베드된 리소스 모두 부재)");
        }
        extractEmbeddedWebapp(extracted);
        return extracted;
    }

    /** 현재 실행이 jar 파일에서 이뤄지고 있는지 (vs IDE/gradle classes 디렉토리). */
    private static boolean isRunningFromJar() {
        try {
            String path = JinieboxStandalone.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath();
            return path != null && path.toLowerCase().endsWith(".jar");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * jar 안의 {@code /webapp/...} 리소스를 디스크에 추출한다.
     * fat jar 빌드 시 src/main/webapp 의 정적 리소스(JSP, HTML, CSS, JS, WEB-INF/web.xml 등)가
     * jar 의 webapp/ 디렉토리에 들어 있다는 전제이다.
     */
    private static void extractEmbeddedWebapp(File targetDir) throws IOException {
        // 가장 단순한 방식: 자신의 jar 파일을 zip 으로 열어 webapp/ prefix 를 추출
        String jarPath = JinieboxStandalone.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath();
        File jarFile = new File(jarPath);
        if (!jarFile.isFile()) {
            throw new IOException("실행 jar 를 찾을 수 없어 webapp 을 추출할 수 없습니다: " + jarPath);
        }

        try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(jarFile)) {
            Enumeration<? extends ZipEntry> entries = zf.entries();
            int count = 0;
            while (entries.hasMoreElements()) {
                ZipEntry e = entries.nextElement();
                String name = e.getName();
                if (!name.startsWith("webapp/")) continue;
                String rel = name.substring("webapp/".length());
                if (rel.isEmpty()) continue;
                File out = new File(targetDir, rel);
                if (e.isDirectory()) {
                    out.mkdirs();
                    continue;
                }
                File parent = out.getParentFile();
                if (parent != null) parent.mkdirs();
                try (InputStream in = zf.getInputStream(e)) {
                    Files.copy(in, out.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                count++;
            }
            System.out.println("[standalone] webapp 리소스 " + count + "개 추출: " + targetDir);
        }
    }
}
