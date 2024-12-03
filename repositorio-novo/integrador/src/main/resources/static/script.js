const modal = document.querySelector('.modal-container');
const tbody = document.querySelector('tbody');
const sNome = document.querySelector('#m-nome');
const sIdade = document.querySelector('#m-idade');
const sTime = document.querySelector('#m-time');
const sSelecao = document.querySelector('#m-selecao');
const sCamisa = document.querySelector('#m-camisa');
const sFoto = document.querySelector('#m-foto');  // Input de imagem
const imgPreview = document.querySelector('#img-preview'); // Tag <img> para exibir a imagem no modal
const btnSalvar = document.querySelector('#btnSalvar');
let itens = [];
let id;

const apiBaseURL = 'https://osdrakedosenai.azurewebsites.net/jogador';
const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB em bytes

// Função para validar o tamanho do arquivo
function isValidFile(file) {
  if (file && file.size > MAX_FILE_SIZE) {
    alert("O arquivo excede o tamanho máximo permitido de 10MB.");
    return false;
  }
  return true;
}

// Abrir o modal
function openModal(edit = false, jogadorId = null) {
  modal.classList.add('active');

  modal.onclick = e => {
    if (e.target.className.indexOf('modal-container') !== -1) {
      modal.classList.remove('active');
    }
  };

  if (edit && jogadorId !== null) {
    const jogador = itens.find(item => item.id === jogadorId);  // Pega pelo id
    if (jogador) {
      sNome.value = jogador.nome;
      sIdade.value = jogador.idade;
      sTime.value = jogador.time;
      sSelecao.value = jogador.selecao;
      sCamisa.value = jogador.camisa;
      id = jogadorId;

      // Exibe a imagem do jogador no modal (se houver uma foto)
      if (jogador.foto) {
        imgPreview.src = jogador.foto;
        imgPreview.style.display = 'block'; // Torna visível a imagem
      } else {
        imgPreview.style.display = 'none'; // Caso não tenha foto, não mostra a imagem
      }
      sFoto.value = ''; // Reseta o campo de arquivo para permitir nova seleção
    }
  } else {
    sNome.value = '';
    sIdade.value = '';
    sTime.value = '';
    sSelecao.value = '';
    sCamisa.value = '';
    sFoto.value = '';  // Limpar o campo de foto
    imgPreview.style.display = 'none'; // Esconde a imagem quando não está em edição
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
        loadItens();  // Atualiza a lista após exclusão
      } else {
        alert('Erro ao excluir jogador');
      }
    })
    .catch(error => alert('Erro na requisição de exclusão: ' + error));
  }
}

// Inserir jogador no HTML
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

// Click para salvar ou atualizar jogador
btnSalvar.onclick = e => {
  e.preventDefault();

  // Verificação dos campos obrigatórios
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
    foto: sFoto.files[0]  // Agora pegamos o arquivo da imagem
  };

  // Verificar o tamanho da foto
  if (!isValidFile(jogador.foto)) {
    return;
  }

  // Criando um FormData para enviar os dados
  const formData = new FormData();
  formData.append('nome', jogador.nome);
  formData.append('idade', jogador.idade);
  formData.append('time', jogador.time);
  formData.append('selecao', jogador.selecao);
  formData.append('camisa', jogador.camisa);

  if (jogador.foto) {
    formData.append('foto', jogador.foto);  // Envia a foto como arquivo
  }

  if (id !== undefined) {
    // Atualização (PUT)
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
    // Adicionar novo jogador (POST)
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

// Listar jogadores
function loadItens() {
  fetch(apiBaseURL)
    .then(response => {
      console.log('Resposta da API:', response);  // Verifique a resposta da API
      return response.json();  // Continue com o processamento normal
    })
    .then(data => {
      console.log('Dados recebidos:', data);  // Verifique os dados recebidos
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

// Carregar jogadores ao iniciar a página
loadItens();
