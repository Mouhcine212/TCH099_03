document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('registerForm');
  const errorMsg = document.getElementById('errorMsg');
  const successMsg = document.getElementById('successMsg');

  // Fonction pour reset les erreurs visuelles
  function clearErrors() {
    errorMsg.textContent = '';
    successMsg.textContent = '';
    form.querySelectorAll('input').forEach(input => {
      input.style.border = 'none';
    });
  }

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearErrors();

    const email = document.getElementById('email').value.trim();
    const motDePasse = document.getElementById('password').value.trim();
    const prenom = document.getElementById('firstName').value.trim();
    const nom = document.getElementById('lastName').value.trim();
    const telephone = document.getElementById('telephone').value.trim();
    const dateNaissance = document.getElementById('dateNaissance').value.trim();
    const passeport = document.getElementById('passeport').value.trim();

    // Stocker les infos supplémentaires en localStorage
    localStorage.setItem('dateNaissance', dateNaissance);
    localStorage.setItem('passeport', passeport);

    // === VALIDATIONS PREMIUM ===
    if (!motDePasse || motDePasse.length < 6) {
      errorMsg.textContent = "Le mot de passe doit contenir au moins 6 caractères.";
      document.getElementById('password').style.border = '2px solid #ff4d4d';
      return;
    }

    if (telephone && !/^\d{3}-\d{3}-\d{4}$/.test(telephone)) {
      errorMsg.textContent = "Format du téléphone invalide (ex: 514-123-4567).";
      document.getElementById('telephone').style.border = '2px solid #ff4d4d';
      return;
    }

    if (!/^[A-Z]{2}\d{6,8}$/i.test(passeport)) {
      errorMsg.textContent = "Numéro de passeport invalide (ex: XA123456).";
      document.getElementById('passeport').style.border = '2px solid #ff4d4d';
      return;
    }

    if (dateNaissance && new Date(dateNaissance) > new Date()) {
      errorMsg.textContent = "La date de naissance ne peut pas être dans le futur.";
      document.getElementById('dateNaissance').style.border = '2px solid #ff4d4d';
      return;
    }

    const userData = {
      email,
      motDePasse,
      prenom,
      nom,
      telephone
    };

    try {
      const res = await fetch('http://localhost/api/user', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
      });

      const data = await res.json();
      console.log(data);

      if (res.ok) {
        successMsg.textContent = "Inscription réussie ! Redirection vers la connexion...";
        form.reset();
        setTimeout(() => {
          window.location.href = 'login.html';
        }, 1500);
      } else {
        errorMsg.textContent = data.error || 'Une erreur est survenue.';
      }

    } catch (err) {
      console.error(err);
      errorMsg.textContent = 'Erreur de connexion au serveur.';
    }
  });
});
