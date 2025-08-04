document.addEventListener('DOMContentLoaded', () => {
  const token = localStorage.getItem('token');
  const profileForm = document.getElementById('profileForm');
  const logoutBtn = document.getElementById('logoutBtn');
  const message = document.getElementById('message');
  const nameInput = document.getElementById('fullName');
  const emailInput = document.getElementById('email');
  const phoneInput = document.getElementById('telephone');

  if (!token) {
    window.location.href = 'login.html';
    return;
  }

  // Fonction pour décoder le JWT
  function parseJwt(token) {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      return null;
    }
  }

  const user = parseJwt(token);
  if (!user) {
    window.location.href = 'login.html';
    return;
  }

  // Pré-remplissage du formulaire
  nameInput.value = user.nom || '';
  emailInput.value = user.email || '';
  phoneInput.value = user.telephone || '';

  // Déconnexion
  logoutBtn.addEventListener('click', () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user_id');
    window.location.href = 'login.html';
  });

  // Validation téléphone
  function isPhoneValid(phone) {
    return /^\d{10}$/.test(phone);
  }

  // === Gestion du bouton Modifier / Confirmer ===
  const editBtn = document.getElementById('saveBtn');
  let editMode = false;

  editBtn.addEventListener('click', (e) => {
    e.preventDefault();

    if (!editMode) {
      // Passe en mode édition
      nameInput.disabled = false;
      emailInput.disabled = false;
      phoneInput.disabled = false;
      editBtn.textContent = "Confirmer";
      editMode = true;
    } else {
      // Soumission réelle du formulaire
      profileForm.requestSubmit();
    }
  });

  // Soumission du formulaire
  profileForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    message.textContent = "";

    const updatedData = {
      nom: nameInput.value.trim(),
      email: emailInput.value.trim(),
      telephone: phoneInput.value.trim()
    };

    if (updatedData.telephone && !isPhoneValid(updatedData.telephone)) {
      message.style.color = "#ff4d4d";
      message.textContent = "Téléphone invalide (10 chiffres requis).";
      return;
    }

    try {
      const res = await fetch('http://localhost/api/update_user', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(updatedData)
      });

      const textResponse = await res.text();
      let data;

      try {
        data = JSON.parse(textResponse);
      } catch (parseErr) {
        console.error("Réponse non JSON:", textResponse);
        message.style.color = "#ff4d4d";
        message.textContent = "Erreur serveur (non JSON).";
        return;
      }

      if (res.ok && data.success) {
        message.style.color = "#14ca50";
        message.textContent = "Profil mis à jour avec succès ! Redirection...";

        // Nouveau token local pour maj navbar immédiatement
        const newTokenPayload = {
          id: user.id,
          email: updatedData.email,
          nom: updatedData.nom,
          telephone: updatedData.telephone,
          exp: Math.floor(Date.now() / 1000) + 3600
        };
        const newToken = generateFakeJWT(newTokenPayload);
        localStorage.setItem('token', newToken);

        // Désactive les champs à nouveau
        nameInput.disabled = true;
        emailInput.disabled = true;
        phoneInput.disabled = true;
        editBtn.textContent = "Modifier";
        editMode = false;

        // Redirection vers l'accueil
        setTimeout(() => {
          window.location.href = 'index.html';
        }, 1500);

      } else {
        message.style.color = "#ff4d4d";
        message.textContent = data.error || "Erreur lors de la mise à jour.";
      }

    } catch (err) {
      console.error(err);
      message.style.color = "#ff4d4d";
      message.textContent = "Erreur serveur.";
    }
  });

  // Génération rapide de faux JWT côté client
  function generateFakeJWT(payload) {
    const header = btoa(JSON.stringify({ alg: "HS256", typ: "JWT" }));
    const body = btoa(JSON.stringify(payload));
    return `${header}.${body}.fake_signature`;
  }
});

// === FORMULAIRE CHANGEMENT DE MOT DE PASSE ===
const passwordForm = document.getElementById('passwordForm');
const passwordMessage = document.getElementById('passwordMessage');

passwordForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  passwordMessage.textContent = '';

  const currentPassword = document.getElementById('currentPassword').value.trim();
  const newPassword = document.getElementById('newPassword').value.trim();
  const confirmPassword = document.getElementById('confirmPassword').value.trim();

  if (!currentPassword || !newPassword || !confirmPassword) {
    passwordMessage.style.color = '#ff4d4d';
    passwordMessage.textContent = "Tous les champs sont obligatoires.";
    return;
  }

  if (newPassword.length < 6) {
    passwordMessage.style.color = '#ff4d4d';
    passwordMessage.textContent = "Le nouveau mot de passe doit contenir au moins 6 caractères.";
    return;
  }

  if (newPassword !== confirmPassword) {
    passwordMessage.style.color = '#ff4d4d';
    passwordMessage.textContent = "Les mots de passe ne correspondent pas.";
    return;
  }

  try {
    const res = await fetch('http://localhost/api/change_password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({
        currentPassword,
        newPassword
      })
    });

    const data = await res.json();

    if (res.ok && data.success) {
      passwordMessage.style.color = "#14ca50";
      passwordMessage.textContent = "Mot de passe modifié avec succès ✅";
      passwordForm.reset();

      // Déconnecter après changement de mot de passe
      setTimeout(() => {
        localStorage.removeItem('token');
        window.location.href = 'login.html';
      }, 1500);

    } else {
      passwordMessage.style.color = "#ff4d4d";
      passwordMessage.textContent = data.error || "Erreur lors du changement de mot de passe.";
    }

  } catch (err) {
    console.error(err);
    passwordMessage.style.color = "#ff4d4d";
    passwordMessage.textContent = "Erreur serveur.";
  }
});

