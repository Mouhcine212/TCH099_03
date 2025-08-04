document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const navUser = document.getElementById('nav-user');
    const navLogin = document.getElementById('nav-login');
    const navRegister = document.getElementById('nav-register');
    const navLogout = document.getElementById('nav-logout');

    if (token) {
        const user = JSON.parse(atob(token.split('.')[1])); 
        navUser.textContent = user.nom || "Mon Profil";
        navUser.style.display = "inline-block";
        navLogout.style.display = "inline-block";
        navLogin.style.display = "none";
        navRegister.style.display = "none";
    } else {
        navUser.style.display = "none";
        navLogout.style.display = "none";
        navLogin.style.display = "inline-block";
        navRegister.style.display = "inline-block";
    }

    navLogout.addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.removeItem('token');
        window.location.href = 'login.html';
    });
});
