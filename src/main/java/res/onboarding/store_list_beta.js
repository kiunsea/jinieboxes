const stepbystep = function () {
    const driver = window.driver.js.driver;
    const driverObj = driver({
        showProgress: true,
        steps: [
            {
                element: "#card-row",
                popover: {
                    title: "스토어 목록",
                    description: "사용자 소유의 스토어 한개와 공유받은 스토어 목록입니다."
                }
            }
            ,{
                element: " #card-row > div > div > div > div",
                popover: {
                    title: "기본 저장소로 지정",
                    description: "기본저장소로 체크하면 박스와 아이템을 등록하고 관리하는 저장소가 됩니다."
                }
            }
            ,{
                element: "#sharedlist",
                popover: {
                    title: "공유받은 스토어 목록",
                    description: "다른 사용자로부터 공유받은 스토어 목록입니다.<br/>스토어 이름과 권한을 보여줍니다."
                }
            }
            ,{
                element: "body > div.container-fluid > div > main > div > div.row.mb-2",
                popover: {
                    title: "공유 스토어 추가",
                    description: "이곳에 스토어 초대코드를 입력하여 다른 사람의 스토어를 공유 목록에 추가 할 수 있습니다."
                }
            }
        ]
    });
    driverObj.drive();
};