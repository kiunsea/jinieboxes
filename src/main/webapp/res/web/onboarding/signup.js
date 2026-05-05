const stepbystep = function () {
    const driver = window.driver.js.driver;
    const driverObj = driver({
        showProgress: true,
        steps: [
            {
                element: "#sns-login",
                popover: {
                    title: "SNS계정으로 간편 로그인",
                    description: "SNS계정을 로그인하여 가입하시면 SNS계정으로 간편하게 로그인 할 수 있습니다."
                }
            }
            ,{
                element: "#juid",
                popover: {
                    title: "일반 회원가입",
                    description: "일반 회원가입인 경우<br/> 등록 신청 후 이메일 인증 절차를 진행합니다."
                }
            }
            ,{
                element: "#user_set > div:nth-child(3)",
                popover: {
                    title: "템플릿 자동 생성",
                    description: "계정 생성시에 선택한 템플릿에 따라 보관함(Box)을 자동 생성합니다."
                }
            }
            ,{
                element: "#user_set > div:nth-child(5)",
                popover: {
                    title: "공유 저장소(Store) 등록",
                    description: "자신의 저장소(Store) 외에 다른 사람으로부터 공유 받은 저장소(Store)의 초대 코드를 입력하여 추가 저장소(Store)를 등록 할 수 있습니다."
                }
            }
        ]
    });
    driverObj.drive();
};