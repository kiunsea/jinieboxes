const stepbystep = function () {
    const driver = window.driver.js.driver;
    const driverObj = driver({
        showProgress: true,
        steps: [
            {
                element: "#automall_div",
                popover: {
                    title: "쇼핑몰 계정 정보 입력",
                    description: "각 쇼핑몰 사이트에 로그인 가능한 계정 정보를 입력하여 주세요.<br/>계정이 쇼핑몰에서 인증되면 브라우저에 계정 정보를 저장합니다."
                }
            }
            ,{
                element: "#autoclass_div",
                popover: {
                    title: "자동화 규칙 목록",
                    description: "쇼핑몰에서 수집한 아이템들을 이곳의 규칙에 따라 자동으로 분류 작업하게 됩니다."
                }
            }
        ]
    });
    driverObj.drive();
};