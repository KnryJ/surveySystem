function renderCurrentUser(currentUser) {
  if (currentUser) {
    document.querySelector('.current-user').textContent = currentUser.username + '，欢迎使用调查问卷系统';
  } else {
    document.querySelector('.current-user').textContent = '系统必须登录后才能使用，请先登录或注册';
  }
}