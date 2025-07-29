document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('registerForm');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = document.getElementById('email').value.trim();
    const motDePasse = document.getElementById('password').value.trim();
    const prenom = document.getElementById('firstName').value.trim();
    const nom = document.getElementById('lastName').value.trim();
    const telephone = document.getElementById('telephone').value.trim();
    const dateNaissance = document.getElementById('dateNaissance').value.trim();
    const passeport = document.getElementById('passeport').value.trim();

    // puisque dans la base sql user na pas de dateNaissance ni passeport, on les stocke dans localStorage.
    localStorage.setItem('dateNaissance', dateNaissance);
    localStorage.setItem('passeport', passeport);

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
        alert("Inscription réussie ! Vous pouvez maintenant vous connecter.");
        window.location.href = 'login.html';
      } else {
        alert(data.error || 'Une erreur est survenue.');
      }

    } catch (err) {
      console.error(err);
      alert('Erreur de connexion au serveur.');
    }
  });
});
