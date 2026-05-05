package com.jiniebox.standalone;

/**
 * Standalone 실행 시 CLI 옵션.
 *
 * <pre>
 * --port=8080            HTTP 포트 (기본 8080)
 * --context=/jbs         Context path (기본 /jbs)
 * --config=path.../JINIEBOX.PROPERTIES   외부 설정 파일 경로 (지정 시 webapp 내부 설정보다 우선)
 * --data-dir=./data      데이터 디렉토리 (logs, tomcat 작업영역, res, 추출된 webapp)
 * --help, -h             도움말
 * </pre>
 */
public final class StandaloneOptions {

    public int port = 8080;
    public String contextPath = "/jbs";
    public String configPath = null;
    public String dataDir = "./data";

    public static StandaloneOptions parse(String[] args) {
        StandaloneOptions o = new StandaloneOptions();
        for (String arg : args) {
            if (arg.startsWith("--port=")) {
                o.port = Integer.parseInt(arg.substring("--port=".length()));
            } else if (arg.startsWith("--context=")) {
                o.contextPath = arg.substring("--context=".length());
            } else if (arg.startsWith("--config=")) {
                o.configPath = arg.substring("--config=".length());
            } else if (arg.startsWith("--data-dir=")) {
                o.dataDir = arg.substring("--data-dir=".length());
            } else if ("--help".equals(arg) || "-h".equals(arg)) {
                printHelp();
                System.exit(0);
            } else {
                System.err.println("알 수 없는 옵션: " + arg);
                printHelp();
                System.exit(1);
            }
        }
        if (!o.contextPath.startsWith("/")) {
            o.contextPath = "/" + o.contextPath;
        }
        return o;
    }

    public void print() {
        System.out.println("=== Jiniebox Standalone ===");
        System.out.println("  port:        " + port);
        System.out.println("  contextPath: " + contextPath);
        System.out.println("  configPath:  " + (configPath != null ? configPath : "(webapp 내장 설정)"));
        System.out.println("  dataDir:     " + dataDir);
        System.out.println("===========================");
    }

    public static void printHelp() {
        System.out.println("Usage: java -jar jiniebox-standalone.jar [options]");
        System.out.println("  --port=8080                            HTTP 포트");
        System.out.println("  --context=/jbs                         Context path");
        System.out.println("  --config=path/to/JINIEBOX.PROPERTIES   외부 설정 파일");
        System.out.println("  --data-dir=./data                      데이터 디렉토리");
        System.out.println("  --help, -h                             이 도움말");
    }
}
