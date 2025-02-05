let maxPage = parseInt(document.querySelector("#forPaging").textContent);
let visiblePage = 0;

$(function () {
    // 初期のページ番号を取得
    let currentPage = parseInt(getQueryParam("page")) || 1;
    refreshPagination(currentPage);
    loadPageContent(currentPage); // ページの内容をロード
});

/**
 * paginationを再生成
 */
function refreshPagination(currentPage) {
    $("#pagination").empty();
    if (currentPage < 1) currentPage = 1;

    // ページネーションの生成
    createPagination(currentPage);
}

/**
 * paginationを生成
 */
function createPagination(currentPage) {
    let startPage = currentPage - visiblePage;
    let endPage = currentPage + visiblePage;

    // paginationの設定
    let pagination = $('<ul class="pagination justify-content-center">');

    // 最初のページ
    let first = createPaginationLink(1, "<<", currentPage !== 1);
    pagination.append(first);

    // 前のページ
    let back = createPaginationLink(currentPage - 1, "<", currentPage > 1);
    pagination.append(back);

    // ページ番号
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

    // 次のページ
    let next = createPaginationLink(currentPage + 1, ">", currentPage < maxPage);
    pagination.append(next);

    // 最後のページ
    let last = createPaginationLink(maxPage, ">>", currentPage !== maxPage);
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
        success: function (data) {
            // ページの内容を更新
            $("#content").html(data); // ここでコンテンツを更新
            // URLを更新
            updateURI(currentPage);

            // コンテンツ更新後にページのリフレッシュ
            window.scrollTo(0, 0);  // スクロール位置を最上部に
        },
        error: function () {
            console.error("ページデータのロードに失敗しました");
        },
    });
}

/**
 * URIを更新する
 */
function updateURI(currentPage) {
    const url = new URL(window.location.href);
    url.searchParams.set("page", currentPage); // クエリパラメータを設定
    history.pushState(null, "", url); // URIを更新（履歴に追加
	if(sessionStorage.getItem("currentPage") !== String(currentPage)){
		sessionStorage.setItem("currentPage",String(currentPage));
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
    let pageItem = $('<li class="page-item">').html(pageLink);
    if (!isEnable || document.getElementById('forPaging').textContent == '0') {
        pageItem.addClass("disabled");
    }
    return pageItem;
}
