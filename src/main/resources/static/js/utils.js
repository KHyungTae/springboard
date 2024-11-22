var kht = {};
//페이징 세팅.
kht.pagination = {
	setData: function(currentPage, totalPages) {
		let htmls = '';
		
		const maxPages = 10; //한번에 표시할 최대 페이지 번호 수
		//현재페이지 기준 시작페이지와 마지막페이지 계산.
		let startPage = Math.max(currentPage - Math.floor(maxPages / 2), 1); //floor() 소수점 이하 버림.
		let endPage = Math.min(startPage + maxPages - 1, totalPages);
		
		//maxPages보다 작으면 1페이지 시작1, 2페이지 시작11, 3페이지 시작21 ...
		if(endPage - startPage + 1 < maxPages) {
			startPage = Math.max(endPage - maxPages + 1, 1);
		}
		
		//이전
		if(currentPage > 1) {
			htmls += '<a href="#" role="button" class="page-link" data-page="'+(startPage)+'"><<</a>';
			htmls += '<a href="#" role="button" class="page-link" data-page="'+(currentPage-1)+'"><</a>';
		}
		
		//페이지 번호
		for(let i = startPage; i <= endPage; i++) {
			let pageLink = '<a href="#" role="button" class="page-link';
			if(i == currentPage) {
				pageLink += ' active';
			}
			pageLink += '" data-page="'+i+'">'+i+'</a>';
			htmls += pageLink;
		}
		//다음
		if(currentPage < totalPages) {
			htmls += '<a href="#" role="button" class="page-link" data-page="'+(currentPage+1)+'">></a>';
			htmls += '<a href="#" role="button" class="page-link" data-page="'+(endPage)+'">>></a>';
		}

		$("#pagination").html(htmls);
	}
};