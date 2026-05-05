const stepbystep = function () {
    const driver = window.driver.js.driver;
    const driverObj = driver({
        showProgress: true,
        steps: [
			{
			    element: "#jinie_char",
			    popover: {
			        title: "무엇이든 물어보세요",
			        description: "지니박스에 대해 자세히 알려드려요"
			    }
			}
			,{
                element: "#store_title",
                popover: {
                    title: "스토어 이름과 사용자 이름입니다",
                    description: "이곳을 클릭하면 박스 목록을 출력합니다"
                }
            }
            ,{
                element: "#btn_toggle_menu",
                popover: {
                    title: "메인 메뉴입니다",
                    description: "스토어와 사용자 정보 메뉴를 제공합니다."
                }
            }
            ,{
                element: "#btn_item_add",
                popover: {
                    title: "아이템 추가 버튼입니다",
                    description: "버튼을 클릭하면 아이템 추가 팝업이 뜹니다"
                }
            }
            ,{
                element: "#card-row-box",
                popover: {
                    title: "이곳에 박스 목록이 출력됩니다",
                    description: "각 박스의 이름과 저장된 아이템의 가지수를 알 수 있습니다"
                }
            }
            ,{
                element: "#card-row-box > div:nth-child(1) > div > div.card-body.btn-toolbar.justify-content-between > button:nth-child(2)",
                popover: {
                    title: "등록된 아이템의 종류 갯수입니다",
                    description: "여기를 클릭하면 아이템 목록이 확장됩니다"
                }
            }
            ,{
                element: "#quick_buttons",
                popover: {
                    title: "빠른 실행 버튼 모음입니다",
                    description: "각 버튼에 마우스를 올리면 버튼에 대해 알 수 있습니다"
                }
            }
            ,{
                element: "#btn_list_top",
                popover: {
                    title: "목록 상단 이동 버튼입니다",
                    description: "화면이 스크롤 상태일때 상단으로 위치를 이동시킬 수 있습니다"
                }
            }
            ,{
                element: "#btn_list_collapse",
                popover: {
                    title: "아이템 목록 보이기/감추기 버튼입니다",
                    description: "각각의 박스에 저장된 아이템 목록들을 전체 펼치거나 접을 수 있습니다"
                }
            }
            ,{
                element: "#btn_onboarding",
                popover: {
                    title: "사용자 도움말입니다",
                    description: "화면상의 주요 요소들을 설명합니다"
                }
            }
            ,{
                element: "#bd-theme",
                popover: {
                    title: "테마 변경 버튼입니다",
                    description: "사용자가 선택한 테마로 변경하여 보여줍니다.<br/> '자동모드'를 선택하면 시간에 따라 테마를 자동으로 변경하여 줍니다"
                }
            }
        ]
    });
    driverObj.drive();
};