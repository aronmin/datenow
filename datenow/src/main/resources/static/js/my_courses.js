document.addEventListener('DOMContentLoaded', function () {
    loadMyCourses();
});

function loadMyCourses() {
    fetch('/api/course/my-course')
    .then(response => {
        if (!response.ok) {
            throw new Error('서버로부터 데이터를 가져오지 못했습니다.');
        }
        return response.json();
    })
    .then(data => {
        const courseList = data.data ?? data;
        renderMyCourses(courseList);
    })
    .catch(error => {
        alert('코스 목록을 불러오지 못했습니다.');
    });
}

function renderMyCourses(courseList) {
    const container = document.getElementById('mycourse-list');
    container.innerHTML = '';

    if (!Array.isArray(courseList) || courseList.length === 0) {
        container.innerHTML = '<p>내가 만든 코스가 없습니다.</p>';
        return;
    }

    courseList.forEach(course => {
        const card = document.createElement('div');
        card.className = 'course-card favorite-card';

        card.innerHTML = `
      <h3>${course.title}</h3>
      <button 
        class="favorite-delete-btn"
        onclick="deactivateCourse(${course.coursesId}, event)">
        ×
      </button>
    `;

        card.addEventListener('click', () => {
            window.location.href = `/my-courses-detail?courseId=${course.coursesId}`;
        });

        container.appendChild(card);
    });
}

function deactivateCourse(courseId, event) {
    event.stopPropagation();

    fetch(`/api/course/${courseId}/deactivate`, {
        method: 'PATCH',
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('코스 삭제 실패');
        }
        return response.text();
    })
    .then(() => {
        alert('코스가 삭제되었습니다.');
        loadMyCourses();
    })
    .catch(err => {
        alert('코스 삭제 중 오류가 발생했습니다.');
    });
}