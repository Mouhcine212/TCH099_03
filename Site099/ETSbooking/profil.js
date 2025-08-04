document.addEventListener('DOMContentLoaded', () => {
  const token = localStorage.getItem('token');
  const profileForm = document.getElementById('profileForm');
  const backBtn = document.getElementById('backBtn');
  const message = document.getElementById('message');
  const nameInput = document.getElementById('fullName');
  const emailInput = document.getElementById('email');
  const phoneInput = document.getElementById('telephone');

  if (!token) {
    window.location.href = 'login.html';
    return;
  }

  // 🔹 Décoder le JWT
  function parseJwt(token) {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      return null;
    }
  }

  let user = parseJwt(token);
  if (!user) {
    window.location.href = 'login.html';
    return;
  }

  // 🔹 Pré-remplissage
  nameInput.value = user.nom || '';
  emailInput.value = user.email || '';
  phoneInput.value = user.telephone || '';

  // 🔹 Bouton retour
  backBtn.addEventListener('click', () => {
    window.location.href = 'index.html';
  });

  // 🔹 Validation téléphone
  function isPhoneValid(phone) {
    return /^\d{10}$/.test(phone);
  }

  // === Gestion du bouton Modifier / Confirmer ===
  const editBtn = document.getElementById('saveBtn');
  let editMode = false;

  editBtn.addEventListener('click', (e) => {
    e.preventDefault();

    if (!editMode) {
      // Active les champs pour édition
      nameInput.disabled = false;
      emailInput.disabled = false;
      phoneInput.disabled = false;
      editBtn.textContent = "Confirmer";
      editMode = true;
    } else {
      // Soumission réelle
      profileForm.requestSubmit();
    }
  });

  // === Soumission du formulaire pour mise à jour ===
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

      const data = await res.json();

      if (res.ok && data.success) {
        // 🔹 Stocker le nouveau token
        if (data.token) {
          localStorage.setItem('token', data.token);
          user = parseJwt(data.token); // met à jour l'objet utilisateur
        }

        message.style.color = "#14ca50";
        message.textContent = "Profil mis à jour avec succès ! Redirection...";

        // Désactive les champs
        nameInput.disabled = true;
        emailInput.disabled = true;
        phoneInput.disabled = true;
        editBtn.textContent = "Modifier";
        editMode = false;

        // Redirection après 1.5s
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
      body: JSON.stringify({ currentPassword, newPassword })
    });

    const data = await res.json();

    if (res.ok && data.success) {
      passwordMessage.style.color = "#14ca50";
      passwordMessage.textContent = "Mot de passe modifié avec succès ✅";
      passwordForm.reset();

      // Déconnexion après changement
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
