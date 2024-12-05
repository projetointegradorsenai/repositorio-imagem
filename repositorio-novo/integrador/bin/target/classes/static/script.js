const modal = document.querySelector('.modal-container');
const tbody = document.querySelector('tbody');
const sNome = document.querySelector('#m-nome');
const sIdade = document.querySelector('#m-idade');
const sTime = document.querySelector('#m-time');
const sSelecao = document.querySelector('#m-selecao');
const sCamisa = document.querySelector('#m-camisa');
const sFoto = document.querySelector('#m-foto');
const btnSalvar = document.querySelector('#btnSalvar');
let itens = [];  
let id;  

const apiBaseURL = 'https://osdrakedosenai.azurewebsites.net/jogador';  

// abrir o modal
function openModal(edit = false, jogadorId = null) {
  modal.classList.add('active');

  modal.onclick = e => {
    if (e.target.className.indexOf('modal-container') !== -1) {
      modal.classList.remove('active');
    }
  };

  if (edit && jogadorId !== null) {
    // adiciona os dados no modal
    const jogador = itens.find(item => item.id === jogadorId);  // pega pelo id
    if (jogador) {
      sNome.value = jogador.nome;
      sIdade.value = jogador.idade;
      sTime.value = jogador.time;
      sSelecao.value = jogador.selecao;
      sCamisa.value = jogador.camisa;
      sFoto.value = jogador.foto;  
      id = jogadorId;  // id pra usar no put
    }
  } else {
    // limpa o modal
    sNome.value = '';
    sIdade.value = '';
    sTime.value = '';
    sSelecao.value = '';
    sCamisa.value = '';
    sFoto.value = '';
    id = undefined; 
  }
}

// editar o jogador
function editItem(jogadorId) {
  openModal(true, jogadorId);
}

// excluir o jogador
function deleteItem(jogadorId) {
  if (confirm('Tem certeza que deseja excluir este jogador?')) {
    fetch(`${apiBaseURL}/${jogadorId}`, {
      method: 'DELETE',
    })
    .then(response => {
      if (response.ok) {
        loadItens();  // atualiza a pagina
      } else {
        alert('Erro ao excluir jogador');
      }
    })
    .catch(error => alert('Erro na requisição de exclusão: ' + error));
  }
}

// inserir html
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

// click para salvar
btnSalvar.onclick = e => {
  e.preventDefault();

  // verificação se os itens estão preenchidos
  if (sNome.value === '' || sIdade.value === '' || sTime.value === '' || sSelecao.value === '' || sCamisa.value === '') {
    alert('Preencha todos os campos.');
    return;
  }

  const fotoURL = sFoto.value.trim();  // aqui é onde armazena a url da imagem

  if (fotoURL === '') {
    alert('Por favor, insira a URL da foto.');
    return;
  }

  const jogador = {
    nome: sNome.value,
    idade: sIdade.value,
    time: sTime.value,
    selecao: sSelecao.value,
    camisa: sCamisa.value,
    foto: fotoURL  
  };

  // Criando um FormData para enviar os dados
  const formData = new FormData();
  formData.append('nome', jogador.nome);
  formData.append('idade', jogador.idade);
  formData.append('time', jogador.time);
  formData.append('selecao', jogador.selecao);
  formData.append('camisa', jogador.camisa);
  formData.append('foto', jogador.foto);

  if (id !== undefined) {
    // Se houver um ID, fazemos uma atualização (PUT)
    fetch(`${apiBaseURL}/${id}`, {
      method: 'PUT',
      body: formData,  // Usando FormData para o envio
    })
    .then(response => {
      if (response.ok) {
        loadItens();  // Atualiza a lista de itens após a edição
        modal.classList.remove('active');
        id = undefined;  // Reseta o ID após a edição
      } else {
        alert('Erro ao atualizar jogador');
      }
    })
    .catch(error => alert('Erro na requisição de atualização: ' + error));
  } else {
    // Caso contrário, estamos adicionando um novo jogador (POST)
    fetch(`${apiBaseURL}/adicionar`, {
      method: 'POST',
      body: formData,  // Usando FormData para o envio
    })
    .then(response => {
      if (response.ok) {
        loadItens();  // Atualiza a lista de itens após a adição
        modal.classList.remove('active');
      } else {
        alert('Erro ao adicionar jogador');
      }
    })
    .catch(error => alert('Erro na requisição de adição: ' + error));
  }
};

//listar a api
function loadItens() {
  fetch(apiBaseURL)
    .then(response => response.json())
    .then(data => {
      console.log('Jogadores recebidos da API:', data); 
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
    .catch(error => alert('Erro ao carregar jogadores: ' + error));
}

// Carregar jogadores ao iniciar a página
loadItens();
