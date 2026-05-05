const stepbystep = function () {
    const driver = window.driver.js.driver;
    const driverObj = driver({
        showProgress: true,
        steps: [
            {
                element: "#gmail",
                popover: {
                    title: "구글 개인 계정 입력",
                    description: "박스와 아이템의 이미지를 사용자 개인의 구글 드라이브에 저장하기 위해서는 구글 계정 인증이 필요합니다."
                }
            }
            ,{
                element: "#card > div:nth-child(6)",
                popover: {
                    title: "보관함(박스) 목록 출력",
                    description: "스토어에 생성한 보관함을 관리합니다.<br/> 이름을 클릭하면 수정 팝업이 뜹니다."
                }
            }
            ,{
                element: "#card > div:nth-child(8)",
                popover: {
                    title: "공유 목록",
                    description: "현재 스토어를 공유받은 사용자 목록입니다."
                }
            }
            ,{
                element: "#card > div:nth-child(10)",
                popover: {
                    title: "초대 목록",
                    description: "현재 스토어에 공유 초대한 사용자 목록입니다.<br/>ADD버튼으로 사용자를 추가 후 사용자가 수락하면 상단의 공유 목록으로 이동합니다."
                }
            }
        ]
    });
    driverObj.drive();
};