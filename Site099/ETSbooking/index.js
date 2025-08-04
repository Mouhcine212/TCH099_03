document.addEventListener('DOMContentLoaded', () => {
    const header = document.querySelector('.header');
    const token = localStorage.getItem('token');

    const loginLink = document.getElementById('loginLink');
    const registerLink = document.getElementById('registerLink');
    const logoutLink = document.getElementById('logoutLink');
    const userName = document.getElementById('userName');

    // Gestion du menu dynamique
    if (token) {
        if (loginLink) loginLink.style.display = 'none';
        if (registerLink) registerLink.style.display = 'none';
        if (logoutLink) logoutLink.style.display = 'inline-block';

        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            if (payload.prenom && payload.nom) {
                userName.textContent = `${payload.prenom} ${payload.nom}`;
                userName.style.display = 'inline-block';
            } else if (payload.nom) {
                userName.textContent = payload.nom;
                userName.style.display = 'inline-block';
            }
        } catch (e) {
            console.error("Impossible de décoder le token JWT :", e);
        }
    } else {
        if (loginLink) loginLink.style.display = 'inline-block';
        if (registerLink) registerLink.style.display = 'inline-block';
        if (logoutLink) logoutLink.style.display = 'none';
        if (userName) userName.style.display = 'none';
    }

    if (logoutLink) {
        logoutLink.addEventListener('click', (e) => {
            e.preventDefault();
            localStorage.removeItem('token');
            localStorage.removeItem('user_id');
            window.location.href = 'index.html';
        });
    }
});
