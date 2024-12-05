const modal = document.querySelector('.modal-container');
const tbody = document.querySelector('tbody');
const sNome = document.querySelector('#m-nome');
const sIdade = document.querySelector('#m-idade');
const sTime = document.querySelector('#m-time');
const sSelecao = document.querySelector('#m-selecao');
const sCamisa = document.querySelector('#m-camisa');
const sFoto = document.querySelector('#m-foto'); 
const imgPreview = document.querySelector('#img-preview');
const btnSalvar = document.querySelector('#btnSalvar');
let itens = [];
let id;

const apiBaseURL = 'https://osdrakedosenai.azurewebsites.net/jogador';
const MAX_FILE_SIZE = 10 * 1024 * 1024;


function isValidFile(file) {
  if (file && file.size > MAX_FILE_SIZE) {
    alert("O arquivo excede o tamanho máximo permitido de 10MB.");
    return false;
  }
  return true;
}


function openModal(edit = false, jogadorId = null) {
  modal.classList.add('active');

  modal.onclick = e => {
    if (e.target.className.indexOf('modal-container') !== -1) {
      modal.classList.remove('active');
    }
  };

  if (edit && jogadorId !== null) {
    const jogador = itens.find(item => item.id === jogadorId);  
    if (jogador) {
      sNome.value = jogador.nome;
      sIdade.value = jogador.idade;
      sTime.value = jogador.time;
      sSelecao.value = jogador.selecao;
      sCamisa.value = jogador.camisa;
      id = jogadorId;

      
      if (jogador.foto) {
        imgPreview.src = jogador.foto;
        imgPreview.style.display = 'block'; 
      } else {
        imgPreview.style.display = 'none'; 
      }
      sFoto.value = ''; 
    }
  } else {
    sNome.value = '';
    sIdade.value = '';
    sTime.value = '';
    sSelecao.value = '';
    sCamisa.value = '';
    sFoto.value = ''; 
    imgPreview.style.display = 'none'; 
    id = undefined;
  }
}

// Editar jogador
function editItem(jogadorId) {
  openModal(true, jogadorId);
}

// Excluir jogador
function deleteItem(jogadorId) {
  if (confirm('Tem certeza que deseja excluir este jogador?')) {
    fetch(`${apiBaseURL}/${jogadorId}`, {
      method: 'DELETE',
    })
    .then(response => {
      if (response.ok) {
        loadItens();  
      } else {
        alert('Erro ao excluir jogador');
      }
    })
    .catch(error => alert('Erro na requisição de exclusão: ' + error));
  }
}


function insertItem(item) {
  let tr = document.createElement('tr');
  tr.innerHTML = `
    <td class="foto">
      <img class="perfil" src="${item.foto}" alt="Foto do Jogador">
    </td>
    <td>${item.nome}</td>
    <td>${item.idade}</td>
    <td>${item.time}</td>
    <td>${item.selecao}</td>
    <td>${item.camisa}</td>
    <td class="acao">
      <button onclick="editItem(${item.id})">
        <img src="./src/icones/7270001 3.svg" class='botaoexcluir'/>
      </button>
    </td>
    <td class="acao">
      <button onclick="deleteItem(${item.id})">
        <img src="./src/icones/484611 2.svg" class='botaoexcluir'/>
      </button>
    </td>
  `;
  tbody.appendChild(tr);
}


btnSalvar.onclick = e => {
  e.preventDefault();

 
  if (sNome.value === '' || sIdade.value === '' || sTime.value === '' || sSelecao.value === '' || sCamisa.value === '') {
    alert('Preencha todos os campos.');
    return;
  }

  const jogador = {
    nome: sNome.value,
    idade: sIdade.value,
    time: sTime.value,
    selecao: sSelecao.value,
    camisa: sCamisa.value,
    foto: sFoto.files[0]  
  };

  
  if (!isValidFile(jogador.foto)) {
    return;
  }

  
  const formData = new FormData();
  formData.append('nome', jogador.nome);
  formData.append('idade', jogador.idade);
  formData.append('time', jogador.time);
  formData.append('selecao', jogador.selecao);
  formData.append('camisa', jogador.camisa);

  if (jogador.foto) {
    formData.append('foto', jogador.foto);  
  }

  if (id !== undefined) {
   
    fetch(`${apiBaseURL}/${id}`, {
      method: 'PUT',
      body: formData, 
    })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => {
          throw new Error(text || 'Erro desconhecido');
        });
      }
      return response.json();
    })
    .then(data => {
      loadItens();  
      modal.classList.remove('active');
      id = undefined; 
    })
    .catch(error => {
      console.error('Erro na requisição de atualização:', error);
      alert('Erro ao atualizar jogador: ' + error.message);
    });
  } else {
    
    fetch(`${apiBaseURL}/adicionar`, {
      method: 'POST',
      body: formData,  
    })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => {
          throw new Error(text || 'Erro desconhecido');
        });
      }
      return response.json();  
    })
    .then(data => {
      loadItens();  
      modal.classList.remove('active');
    })
    .catch(error => {
      console.error('Erro ao adicionar jogador:', error);
      alert('Erro ao adicionar jogador: ' + error.message);
    });
  }
};


function loadItens() {
  fetch(apiBaseURL)
    .then(response => {
      console.log('Resposta da API:', response); 
      return response.json();  
    })
    .then(data => {
      console.log('Dados recebidos:', data);  
      if (data && Array.isArray(data)) {
        itens = data; 
        tbody.innerHTML = '';  
        if (itens.length > 0) {
          itens.forEach(item => insertItem(item)); 
        }
      } else {
        alert('Erro ao carregar os dados da API');
      }
    })
    .catch(error => {
      console.error('Erro ao carregar jogadores:', error);
      alert('Erro ao carregar jogadores: ' + error);
    });
}


loadItens();
