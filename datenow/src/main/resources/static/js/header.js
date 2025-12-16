document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.logout-btn').forEach(btn => {
    btn.addEventListener('click', async () => {
      if (!confirm('로그아웃 하시겠습니까?')) return;

      try {
        const res = await fetch('/auth/logout', {
          method: 'POST',
          credentials: 'include'
        });

        if (!res.ok) {
          alert('로그아웃에 실패했습니다.');
          return;
        }

        window.location.href = '/main';
      } catch (e) {
        console.error(e);
        alert('로그아웃 중 오류가 발생했습니다.');
      }
    });
  });
});