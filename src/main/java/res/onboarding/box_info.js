const stepbystep = function () {
    const driver = window.driver.js.driver;
    const driverObj = driver({
        showProgress: true,
        steps: [
            {
                element: "#card-row-image",
                popover: {
                    title: "박스의 이미지를 저장하여 출력 할 수 있습니다",
                    description: "이미지 출력 영역이 보이지 않는다면 스토어 상세 설정에서 '박스와 아이템 이미지 사용'으로 이미지 저장 기능을 사용 할 수 있습니다."
                }
            }
            ,{
                element: "#card_title_detail",
                popover: {
                    title: "박스의 이름과 상세입니다",
                    description: "박스 이름과 상세 설정은 스토어 상세의 박스 목록에서 수정 할 수 있습니다."
                }
            }
            ,{
                element: "#item-list",
                popover: {
                    title: "아이템 목록입니다",
                    description: "박스에 저장한 아이템 목록입니다."
                }
            }
            ,{
                element: "#btn_item_batch",
                popover: {
                    title: "여러개의 아이템을 수정하거나 삭제합니다",
                    description: "아이템 목록의 체크박스를 체크하여 여러개의 아이템을 한번에 수정하거나 삭제 할 수 있습니다."
                }
            }
            ,{
                element: "#card > div:nth-child(5)",
                popover: {
                    title: "공유 목록",
                    description: "현재 박스를 공유받은 사용자 목록입니다."
                }
            }
            ,{
                element: "#card > div:nth-child(7)",
                popover: {
                    title: "초대 목록",
                    description: "현재 박스에 공유 초대한 사용자 목록입니다.<br/>ADD버튼으로 사용자를 추가 후 사용자가 수락하면 상단의 공유 목록으로 이동합니다."
                }
            }
        ]
    });
    driverObj.drive();
};