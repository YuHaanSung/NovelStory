// src/main/resources/static/js/header.js (전체 덮어쓰기)

document.addEventListener("DOMContentLoaded", function() {
    // 1. 헤더 UI (검색창 input 수정됨)
    const headerHTML = `
    <header class="fixed top-0 left-0 w-full z-50 bg-white border-b border-gray-200 shadow-sm font-sans h-16">
        <div class="max-w-7xl mx-auto px-4 h-full flex items-center justify-between">
            <div class="flex items-center gap-2 cursor-pointer" onclick="location.href='index.html'">
                <div class="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center text-white font-bold text-xl">N</div>
                <span class="text-xl font-bold tracking-tight text-indigo-900">NovelHub</span>
            </div>
            
            <div class="hidden md:flex flex-1 max-w-lg mx-8 relative">
                <input type="text" id="search-input" placeholder="작품이나 작가를 검색해보세요 (Enter)" 
                       onkeyup="if(window.event.keyCode==13){searchNovel()}"
                       class="w-full pl-10 pr-4 py-2 rounded-full bg-gray-100 border-none focus:ring-2 focus:ring-indigo-500 focus:bg-white transition-all outline-none text-sm">
                <svg class="absolute left-3 top-2.5 w-4 h-4 text-gray-400 cursor-pointer" onclick="searchNovel()" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
            </div>

            <div class="flex items-center gap-3" id="global-auth-menu"></div>
        </div>
    </header>
    `;

    document.body.insertAdjacentHTML('afterbegin', headerHTML);
    document.body.style.paddingTop = "64px";

    // 2. 로그인 로직 (기존 유지)
    const authorId = localStorage.getItem('loginAuthorId');
    const menuContainer = document.getElementById('global-auth-menu');

    if (authorId) {
        // 서버 확인 로직
        fetch('/api/authors/' + authorId)
            .then(res => {
                if (!res.ok) throw new Error();
                return res.text();
            })
            .then(text => {
                if (!text) throw new Error("User not found");
                menuContainer.innerHTML = `
                    <button onclick="location.href='novel_create.html'" class="hidden sm:flex items-center gap-1 text-gray-600 hover:text-indigo-600 font-medium transition-colors text-sm">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path></svg>
                        <span>글쓰기</span>
                    </button>
                    <div class="h-4 w-px bg-gray-300 mx-1 hidden sm:block"></div>
                    <button onclick="location.href='writer_main.html'" class="flex items-center gap-2 bg-indigo-50 text-indigo-700 px-3 py-1.5 rounded-full font-bold hover:bg-indigo-100 transition text-sm">
                        <span>내 서재</span>
                    </button>
                    <button onclick="globalLogout()" class="text-sm text-gray-400 hover:text-red-500">로그아웃</button>
                `;
            })
            .catch(() => {
                console.log("서버 재시작 감지: 자동 로그아웃");
                localStorage.removeItem('loginAuthorId');
                window.location.reload();
            });

    } else {
        menuContainer.innerHTML = `
            <button onclick="location.href='login.html'" class="text-gray-600 hover:text-indigo-600 font-medium text-sm">로그인</button>
            <button onclick="location.href='signup.html'" class="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-full font-medium transition shadow-md text-sm">
                시작하기
            </button>
        `;
    }
});

// [추가] 검색 실행 함수
function searchNovel() {
    const keyword = document.getElementById('search-input').value;
    if (keyword) {
        // index.html로 키워드를 들고 이동
        location.href = `index.html?keyword=${encodeURIComponent(keyword)}`;
    } else {
        alert("검색어를 입력해주세요.");
    }
}

function globalLogout() {
    if(confirm('로그아웃 하시겠습니까?')) {
        localStorage.removeItem('loginAuthorId');
        location.href = 'index.html';
    }
}