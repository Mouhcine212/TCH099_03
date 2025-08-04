document.getElementById('loginForm').addEventListener('submit', async function (e) {
  e.preventDefault();

  const courriel = document.getElementById('email').value.trim();
  const mot_de_passe = document.getElementById('motDePasse').value;

  try {
    const response = await fetch(
      'https://flightets-gghremf5czh9d3ea.canadacentral-01.azurewebsites.net/api/endpoints/login.php',
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ courriel, mot_de_passe }),
      }
    );

    const data = await response.json();

    if (response.ok && data.token) {
      localStorage.setItem('token', data.token);
      window.location.href = 'index.html';
    } else {
      document.getElementById('errorMsg').textContent = data.error || 'Identifiants invalides';
    }
  } catch (error) {
    console.error('Erreur réseau :', error);
    document.getElementById('errorMsg').textContent = 'Erreur serveur';
  }
});
