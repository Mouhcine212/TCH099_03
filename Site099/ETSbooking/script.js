document.getElementById('search-form').addEventListener('submit', function (e) {
  e.preventDefault();
  const destination = e.target.destination.value;
  const date = e.target.date.value;

  fetch(`backend/vols.php?destination=${destination}&date=${date}`)
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
