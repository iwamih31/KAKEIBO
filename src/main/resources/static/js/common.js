/**
 *
 */

// id='head'のエレメントを取得
let head_element = document.getElementById('head');
// クエリパラメータを取得
let param_map = query_param_to_map(param);

// クエリパラメータにsectionがあれば
if (param_map.get('section')) {
	// クエリパラメータに応じたcssに切り替える
	if (param_map.get('section') === '予算') {
	    head_element.classList.replace('bg_skyblue', 'bg_pink');
	    head_element.style.backgroundColor = 'pink';
	}
}


// クエリパラメータを取得してMAPにする
function query_param_to_map(param) {
    param = param.replace('?', '');
    let param_splitted = param.split('&');
    let param_map = new Map();
    param_splitted.forEach(val => {
        let splitted_val = val.split('=');
        param_map.set(splitted_val[0], splitted_val[1]);
    });
    return param_map;
}


 var n3 = document.getElementsByClassName('num3');
for (var i = 0; n3.length; i++){
	let p = n3[i].textContent.replace('\xA5', '');
	if(isFinite(p)){
		n3[i].innerHTML = Number(p).toLocaleString('ja-JP', {"style":"currency", "currency":"JPY"});
	}
}

function changeStyle() {
	// 現在のURLからクエリパラメータを取得
	const urlParams = new URLSearchParams(window.location.search);
	const section_value = urlParams.get("section");
	const header_element = document.getElementById("head");
	if (section_value === '予算') {
	    header_element.classList.replace('bg_skyblue', 'bg_pink');
	}
}
