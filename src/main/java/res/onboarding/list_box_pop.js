const stepbystep_pop = function () {
    const driver = window.driver.js.driver;
    const driverObj = driver({
        showProgress: true,
        steps: [
            {
                element: "#mdai_div_barcode_btn",
                popover: {
                    title: "바코드 스캔이 가능합니다",
                    description: "상품의 바코드가 있다면 카메라로 바코드를 스캔하여 상품의 이름을 자동으로 입력 할 수 있습니다."
                }
            }
            ,{
                element: "#nav-barcode > div:nth-child(7)",
                popover: {
                    title: "등록할 박스를 지정합니다",
                    description: "박스 목록 화면에서 아이템을 등록시엔 어느 박스에 저장하는지 지정해야 합니다."
                }
            }
            ,{
                element: "#mdai_div_addimg",
                popover: {
                    title: "아이템의 이미지를 저장 할 수 있습니다",
                    description: "이미지 저장 항목이 보이지 않는다면 스토어 상세 설정에서 '박스와 아이템 이미지 사용'으로 이미지 저장 기능을 사용 할 수 있습니다."
                }
            }
            ,{
                element: "#mdai_add_cont_div",
                popover: {
                    title: "연속해서 아이템을 등록 할 수 있습니다",
                    description: "체크하면 아이템 입력시마다 팝업을 닫지 않고 연속해서 아이템을 등록 할 수 있습니다."
                }
            }
        ]
    });
    driverObj.drive();
};