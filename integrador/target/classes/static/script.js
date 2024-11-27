const modal = document.querySelector('.modal-container');
const tbody = document.querySelector('tbody');
const sNome = document.querySelector('#m-nome');
const sIdade = document.querySelector('#m-idade');
const sTime = document.querySelector('#m-time');
const sSelecao = document.querySelector('#m-selecao');
const sCamisa = document.querySelector('#m-camisa');
const sFoto = document.querySelector('#m-foto');
const btnSalvar = document.querySelector('#btnSalvar');   
let itens = [];  // Este é o array que armazena os jogadores
let id;  // Esta variável armazena o id do jogador a ser editado

const apiBaseURL = 'http://localhost:8080/jogador';  // URL da API

// Defina o limite de tamanho de arquivo (10MB neste exemplo)
const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

// Função para abrir o modal, seja para edição ou para adicionar um novo jogador
function openModal(edit = false, jogadorId = null) {
  modal.classList.add('active');

  modal.onclick = e => {
    if (e.target.className.indexOf('modal-container') !== -1) {
      modal.classList.remove('active');
    }
  };

  if (edit && jogadorId !== null) {
    // Carregar os dados do jogador para edição
    const jogador = itens.find(item => item.id === jogadorId);  // Encontra o jogador pelo ID
    if (jogador) {
      sNome.value = jogador.nome;
      sIdade.value = jogador.idade;
      sTime.value = jogador.time;
      sSelecao.value = jogador.selecao;
      sCamisa.value = jogador.camisa;
      sFoto.value = '';  // Foto não pode ser preenchida via JS diretamente
      id = jogadorId;  // Armazena o ID para usá-lo no PUT
    }
  } else {
    // Limpar os campos para adicionar um novo jogador
    sNome.value = '';
    sIdade.value = '';
    sTime.value = '';
    sSelecao.value = '';
    sCamisa.value = '';
    sFoto.value = '';
    id = undefined;  // Reseta o ID para um novo jogador
  }
}

// Função para editar jogador
function editItem(jogadorId) {
  openModal(true, jogadorId);
}

// Função para excluir jogador
function deleteItem(jogadorId) {
  if (confirm('Tem certeza que deseja excluir este jogador?')) {
    fetch(`${apiBaseURL}/${jogadorId}`, {
      method: 'DELETE',
    })
    .then(response => {
      if (response.ok) {
        loadItens();  // Atualiza a lista de jogadores após a exclusão
      } else {
        alert('Erro ao excluir jogador');
      }
    })
    .catch(error => alert('Erro na requisição de exclusão: ' + error));
  }
}

// Função para inserir um jogador na tabela (para exibição)
function insertItem(item) {
  console.log('Foto do jogador:', item.foto);  // Verificando o valor de item.foto

  let tr = document.createElement('tr');
  tr.innerHTML = `
    <td class="foto">
      <img src="http://localhost:8080/imagens/${item.foto}"   alt="Foto do Jogador">
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

// Evento de clique no botão "Salvar" para adicionar ou editar um jogador
btnSalvar.onclick = e => {
  e.preventDefault();

  // Verificar se os campos obrigatórios foram preenchidos
  if (sNome.value == '' || sIdade.value == '' || sTime.value == '' || sSelecao.value == '' || sCamisa.value == '') {
    alert('Preencha todos os campos.');
    return;
  }

  // Verificar o tamanho do arquivo da foto
  const file = sFoto.files[0];
  if (file && file.size > MAX_FILE_SIZE) {
    alert('O tamanho do arquivo é muito grande. O limite é 10MB.');
    return;
  }

  const jogador = {
    nome: sNome.value,
    idade: sIdade.value,
    time: sTime.value,
    selecao: sSelecao.value,
    camisa: sCamisa.value,
    foto: file  // Agora estamos usando FileList para pegar o arquivo da foto
  };

  const formData = new FormData();
  formData.append('nome', jogador.nome);
  formData.append('idade', jogador.idade);
  formData.append('time', jogador.time);
  formData.append('selecao', jogador.selecao);
  formData.append('camisa', jogador.camisa);
  if (jogador.foto) {
    formData.append('foto', jogador.foto);  // Foto será enviada junto aos outros dados
  }

  if (id !== undefined) {
    // Se id estiver definido, significa que é uma edição (PUT)
    fetch(`${apiBaseURL}/${id}`, {
      method: 'PUT',
      body: formData,  // Usamos FormData para enviar o arquivo
    })
    .then(response => {
      if (response.ok) {
        loadItens();  // Atualiza a lista de jogadores após a edição
        modal.classList.remove('active');
        id = undefined;  // Reseta o ID após a edição
      } else {
        alert('Erro ao atualizar jogador');
      }
    })
    .catch(error => alert('Erro na requisição de atualização: ' + error));
  } else {
    // Se id não estiver definido, é um novo jogador (POST)
    fetch(`${apiBaseURL}/adicionar`, {
      method: 'POST',
      body: formData,  // Usamos FormData para enviar o arquivo
    })
    .then(response => {
      if (response.ok) {
        loadItens();  // Atualiza a lista de jogadores após adicionar
        modal.classList.remove('active');
      } else {
        alert('Erro ao adicionar jogador');
      }
    })
    .catch(error => alert('Erro na requisição de adição: ' + error));
  }
};

// Função para carregar todos os jogadores da API
function loadItens() {
  fetch(apiBaseURL)
    .then(response => response.json())
    .then(data => {
      console.log('Jogadores recebidos da API:', data);  // Verifique o que está sendo retornado pela API
      itens = data;  // Atualiza a lista de jogadores
      tbody.innerHTML = '';  // Limpa a tabela
      itens.forEach(item => insertItem(item));  // Insere todos os jogadores na tabela
    })
    .catch(error => alert('Erro ao carregar jogadores: ' + error));
}

// Carregar jogadores ao iniciar a página
loadItens();
