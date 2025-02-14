/**
 *
 * 
 **/

let maxPage = parseInt(document.querySelector("#forPaging").textContent) + 1;
let search = document.querySelector("#forSearch").textContent;
let visiblePage = 0;

$(function() {
	// 初期のページ番号を取得
	//Postで得た状態保持変数があった場合は優先する。
	let currentPage = parseInt(getQueryParam("page")) || 1;
	currentPage = parseInt(document.querySelector("#forPagingCurrentPage").textContent)+1 || currentPage;
		window.history.pushState({}, '', '/articles/?page=' + currentPage);
	refreshPagination(currentPage);
	if($("#forSearch").text() == ''){
	loadPageContent(currentPage); // ページの内容をロード
	} else{
		//searchパターンのための1行
		//searchedPageContent(currentPage);
	}
});

/**
 * 遷移時にpaginationを再生成
 *
 */
function refreshPagination(currentPage) {
	$("#pagination").empty();
	if (currentPage < 1) {
		currentPage = 1;
	}
	// ページネーションの生成
	createPagination(currentPage);
}

/**
 * paginationを生成
 */

function createPagination(currentPage) {
	let startPage = currentPage - visiblePage;
	let endPage = currentPage + visiblePage;

	//startPage == endPage == currentPage
	// paginationの設定
	let pagination = $('<ul class="pagination justify-content-center">');

	// 最初のページ
	let first = createPaginationLink(1, "<<", currentPage !== 1);
	pagination.append(first);

	// 前のページ
	let back = createPaginationLink(currentPage - 1, "<", currentPage > 1);
	pagination.append(back);

	// ページ番号
	/**
	 * startPage == endPageの時、
	 *どんな負の値をとっても(1,1)のペアになる。 (1,-80+81),(1,-30+31)...
	 *つまり startPage == endPageは変わりません。
	 * 
	 **/

	 	if (startPage < 1) {
		endPage += -(startPage - 1);
		startPage = 1;
	}
	if (endPage > maxPage) {
		startPage -= endPage - maxPage;
		startPage = Math.max(startPage, 1);
		endPage = maxPage;
	}

	for (let i = startPage; i <= endPage && i <= maxPage; i++) {
		let number = createPaginationLink(i, i, true);
		if (i === currentPage) {
			number.addClass("active");
			number.css("pointer-events", "none");
		}
		pagination.append(number);
	}
	//この辺境界値周りだから実装は気を付ける。

	// 次のページ
	let next = createPaginationLink(currentPage + 1, ">", currentPage < maxPage);
	pagination.append(next);

	// 最後のページ 
	let last = createPaginationLink(maxPage, ">>", currentPage < maxPage);
	pagination.append(last);

	// 生成したpaginationを設定
	$("#pagination").html(pagination);
}

/**
 * ページの内容をロードする
 */
function loadPageContent(currentPage) {
	$.ajax({
		url: `/articles/?page=${currentPage}`,
		method: "GET",
		success: function(data) {
			// ページの内容を更新
			updateURI(currentPage);

			// コンテンツ更新後にページのリフレッシュ
			window.scrollTo(0, 0);  // スクロール位置を最上部に
		},
		error: function() {
			console.error("ページデータのロードに失敗しました");
		},
	});
}

/** 
 *
 * 拡張時には可変引数で対応、合致する引数名が無ければ特に何もしない。
 * 上記ページロード関数で検索などの変数が関係してくる場合に使用する。
 * クエリパラメータで対応する場合はGETで対応できる。
 * 検索フォームの該当inputに値を入れて単純にPostをする形式。

 * 空文字対応をコントローラ側で徹底すること。
 **/
function searchedPageContent(currentPage = 1) {	
	
		// POSTリクエストで送信するデータ	    
	$("#searchWords").val(search);
	$("#searchPage").val(currentPage);
	$("#searchForm").submit();
	/*
	incrementとdecrementを区別する。
	
	 */
//	updateURI(2);
}

/**
 * URIを更新する
 */
function updateURI(currentPage) {
	const url = new URL(window.location.href);
	url.searchParams.set("page", currentPage); // クエリパラメータを設定
	history.pushState(null, "", url); // URIを更新（履歴に追加
	if (sessionStorage.getItem("currentPage") !== String(currentPage)) {
		sessionStorage.setItem("currentPage", String(currentPage));
		window.location.href = '/articles/?page=' + currentPage;
	}
}

/*
 * URLから指定したパラメータを取得
 */
function getQueryParam($key) {
	if (1 < document.location.search.length) {
		const query = document.location.search.substring(1);
		const parameters = query.split("&");
		for (let i = 0; i < parameters.length; i++) {
			// パラメータ名とパラメータ値に分割する
			const element = parameters[i].split("=");
			if (element[0] === $key) {
				return element[1];
			}
		}
	}
	return null;
}

/*
 * paginationのリンクを生成
 */
function createPaginationLink(currentPage, label, isEnable) {
	let pageLink = $(
		`<a class="page-link" href="javascript:loadPageContent(${currentPage});">`
	).html(
		`<div class="text-center" style="width: 1.5rem">${label}</div>`
	);
	if(search != ""){
		pageLink = $(
				`<a class="page-link" href="javascript:searchedPageContent(${currentPage});">`
			).html(
				`<div class="text-center" style="width: 1.5rem">${label}</div>`
			);
	}
	let pageItem = $('<li class="page-item">').html(pageLink);
	if (!isEnable || document.getElementById('forPaging').textContent == '0') {
		pageItem.addClass("disabled");
	}
	return pageItem;
}
