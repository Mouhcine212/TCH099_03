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

  nameInput.value = user.nom || '';
  emailInput.value = user.email || '';
  phoneInput.value = user.telephone || '';

  backBtn.addEventListener('click', () => {
    window.location.href = 'index.html';
  });

  function isPhoneValid(phone) {
    return /^\d{10}$/.test(phone);
  }

  const editBtn = document.getElementById('saveBtn');
  let editMode = false;

  editBtn.addEventListener('click', (e) => {
    e.preventDefault();

    if (!editMode) {
      nameInput.disabled = false;
      emailInput.disabled = false;
      phoneInput.disabled = false;
      editBtn.textContent = "Confirmer";
      editMode = true;
    } else {
      profileForm.requestSubmit();
    }
  });

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
      const res = await fetch('https://flightets-gghremf5czh9d3ea.canadacentral-01.azurewebsites.net/api/update_user', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(updatedData)
      });

      const data = await res.json();

      if (res.ok && data.success) {
        if (data.token) {
          localStorage.setItem('token', data.token);
          user = parseJwt(data.token); 
        }

        message.style.color = "#14ca50";
        message.textContent = "Profil mis à jour avec succès ! Redirection...";

        nameInput.disabled = true;
        emailInput.disabled = true;
        phoneInput.disabled = true;
        editBtn.textContent = "Modifier";
        editMode = false;

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
    const res = await fetch('https://flightets-gghremf5czh9d3ea.canadacentral-01.azurewebsites.net/api/change_password', {
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
      passwordMessage.textContent = "Mot de passe modifié avec succès";
      passwordForm.reset();

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
