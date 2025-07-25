// === GESTION DU BOUTON CONNEXION / DÉCONNEXION ===
const token = localStorage.getItem('jwt');
const authBtnContainer = document.getElementById('authBtn');

if (authBtnContainer) {
  if (token) {
    authBtnContainer.innerHTML = `<a href="#" id="logoutBtn"><i class="fas fa-sign-out-alt"></i> Déconnexion</a>`;
    const logoutBtn = document.getElementById('logoutBtn');
    logoutBtn.addEventListener('click', (e) => {
      e.preventDefault();
      localStorage.removeItem('jwt');
      localStorage.setItem('flash', 'Déconnexion réussie ✅');
      window.location.href = '/etsbooking/login.html';
    });
  } else {
    authBtnContainer.innerHTML = `<a href="/etsbooking/login.html"><i class="fas fa-user"></i> Connexion</a>`;
  }
}

// === AFFICHAGE DU MESSAGE DE DÉCONNEXION SUR login.html ===
const flash = localStorage.getItem('flash');
if (flash) {
  const successMsg = document.getElementById('successMsg');
  if (successMsg) {
    successMsg.textContent = flash;
  }
  localStorage.removeItem('flash');
}

// === LOGIN.JS ===
const loginForm = document.getElementById('loginForm');
if (loginForm) {
  const errorMsg = document.getElementById('errorMsg');
  const successMsg = document.getElementById('successMsg');

  loginForm.addEventListener('submit', async function (e) {
    e.preventDefault();

    errorMsg.textContent = '';
    successMsg.textContent = '';

    const email = loginForm.email.value;
    const motDePasse = loginForm.motDePasse.value;

    try {
      const response = await fetch('http://localhost/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, motDePasse })
      });

      const data = await response.json();
      console.log("Réponse serveur:", data);

      if (response.ok && data.token) {
        localStorage.setItem('jwt', data.token);
        successMsg.textContent = "Connexion réussie ! Redirection...";
        setTimeout(() => {
          window.location.href = '/etsbooking/index.html';
        }, 1000);
      } else {
        errorMsg.textContent = data.error || "Identifiants invalides.";
      }
    } catch (err) {
      errorMsg.textContent = "Erreur réseau ou serveur.";
      console.error("Erreur:", err);
    }
  });
}

// === INDEX.JS ===
const searchForm = document.getElementById('search-form');
if (searchForm) {
  if (!token) {
    window.location.href = '/etsbooking/login.html';
  }

  searchForm.addEventListener('submit', function (e) {
    e.preventDefault();
    const destination = e.target.destination.value;
    const date = e.target.date.value;

    fetch(`/etsbooking/backend/vols.php?destination=${destination}&date=${date}`, {
      headers: {
        'Authorization': 'Bearer ' + token
      }
    })
      .then(res => res.json())
      .then(data => {
        const results = document.getElementById('results');
        results.innerHTML = '';
        data.forEach(vol => {
          results.innerHTML += `
            <div>
              <strong>Vol ${vol.numero_vol}</strong> - ${vol.destination}, ${vol.date} à ${vol.heure}<br>
              Prix: ${vol.prix}$ - Dispo: ${vol.disponibilite}
            </div><hr>
          `;
        });
      });
  });
}
// === GESTION DE L’INSCRIPTION ===
const registerForm = document.getElementById('registerForm');
if (registerForm) {
  const registerMsg = document.getElementById('registerMsg');

  registerForm.addEventListener('submit', async function (e) {
    e.preventDefault();

    registerMsg.textContent = '';

    const email = registerForm.email.value;
    const password = registerForm.password.value;
    const firstName = registerForm.firstName.value;
    const lastName = registerForm.lastName.value;

    try {
      const response = await fetch('http://localhost/api/user', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, firstName, lastName })
      });

      const data = await response.json();
      console.log(data);

      if (response.ok && data.success) {
        registerMsg.textContent = "Compte créé avec succès ! Redirection...";
        setTimeout(() => {
          window.location.href = "login.html";
        }, 1500);
      } else {
        registerMsg.textContent = data.error || "Erreur lors de l'inscription.";
      }
    } catch (err) {
      registerMsg.textContent = "Erreur réseau ou serveur.";
      console.error("Erreur:", err);
    }
  });
}
