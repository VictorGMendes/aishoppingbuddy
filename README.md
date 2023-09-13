## Diagrama de Classes
![DIAGRAMA](diagrama_uml.png)

## Endpoints
- Funcionário
  - [Cadastro de Funcionário](#cadastro-de-funcionário)
  - [Login de Funcionário](#login-de-funcionário)
  - [Listar todos Funcionários](#listar-todos-funcionários)
  - [Detalhar Funcionário](#detalhar-funcionário)
  - [Apagar Funcionário](#apagar-funcionário)
  - [Atualizar Funcionário](#atualizar-funcionário)
- Parceiro de Negócios
  - [Cadastrar Parceiro](#cadastrar-parceiro)
  - [Listar todos Parceiros](#listar-todos-parceiros)
  - [Detalhar Parceiro](#detalhar-parceiro)
  - [Apagar Parceiro](#apagar-parceiro)
  - [Atualizar Parceiro](#atualizar-parceiro)
- Usuário
  - [Cadastrar Usuário](#cadastrar-usuário)
  - [Listar todos Usuários](#listar-todos-usuários)
  - [Detalhar Usuário](#detalhar-usuário)
  - [Apagar Usuário](#apagar-usuário)
  - [Atualizar Usuário](#atualizar-usuário)
  - [Buscar Usuários por Nome](#buscar-usuários-por-nome)
- Produto
  - [Cadastrar Produto](#cadastrar-produto)
  - [Listar todos Produtos](#listar-todos-produtos)
  - [Detalhar Produto](#detalhar-produto)
  - [Apagar Produto](#apagar-produto)
  - [Atualizar Produto](#atualizar-produto)
  - [Buscar Produtos por nome](#buscar-produtos-por-nome)
- Recomendação
  - [Gerar Recomendação](#gerar-recomendação)
  - [Listar todas Recomendações](listar-todas-recomendações)
  - [Buscar Recomendações por título](buscar-recomendações-por-título)

## FUNCIONÁRIO

### Cadastro de Funcionário

`POST` /aishoppingbuddy/api/funcionario/cadastrar/{idParceiro}

*Campos de requisição*

| campo | tipo   | obrigatório | descrição            |
| ----- | ------ | :---------: | -------------------- |
| nome  | String |     sim     | Nome do Funcionário  |
| email | String |     sim     | Email do Funcionário |
| senha | String |     sim     | Senha do Funcionário |

*Exemplo de requisição*
```
{
  "nome": "Lucas Veríssimo",
  "email": "lucas@gmail.com",
  "senha": "senhaDoLucas"
}
```

*Resposta*

| código | descrição                                |
| ------ | ---------------------------------------- |
| 201    | o funcionário foi cadastrado com sucesso |
| 400    | dados inválidos                          |

### Login de Funcionário

`POST` /aishoppingbuddy/api/funcionario/login

*Campos de requisição*

| campo | tipo   | obrigatório | descrição            |
| ----- | ------ | :---------: | -------------------- |
| email | String |     sim     | Email do Funcionário |
| senha | String |     sim     | Senha do Funcionário |

*Exemplo de requisição*
```
{
  "email": "lucas@gmail.com",
  "senha": "senhaDoLucas"
}
```

*Exemplo de Resposta*
{
	"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlbWFpbDFAZ21haWwuY29tIiwiaXNzIjoiQUlTaG9wcGluZ0J1ZGR5IiwiZXhwIjoxNjk0MzkzNDU1fQ.k1fZOy305rNnhbo5F3NHVx1fXFaVplnHYtjHW4tfPNY",
	"type": "JWT",
	"prefix": "Bearer"
}

Essa requisição retorna um Token que é necessário para fazer qualquer outra requisição além dessa e do cadastro.

*Resposta*

| código | descrição                            |
| ------ | ------------------------------------ |
| 201    | o funcionário foi logado com sucesso |
| 400    | dados inválidos                      |

### Listar todos Funcionários

`GET` /aishoppingbuddy/api/funcionario

*Exemplo de resposta*
```
[
	{
		"id": 1,
		"nome": "Yago Leandro Martin Lima",
		"email": "yago-lima87@doublemoore.com",
		"senha": "$2a$10$h61CPmiI/GZz/U0Gm1LZwe1G/pttEnEi0Bvf3HasKUVPMvBKlRLja",
		"parceiro": {
			"id": 1,
			"nomeFantasia": "Amazon",
			"dataEntrada": [
				2023,
				9,
				11
			],
			"dataSaida": null,
			"cnpj": "38345431000162"
		},
		"enabled": true,
		"credentialsNonExpired": true,
		"accountNonExpired": true,
		"authorities": [
			{
				"authority": "ROLE_USUARIO"
			}
		],
		"username": "yago-lima87@doublemoore.com",
		"accountNonLocked": true,
		"password": "$2a$10$h61CPmiI/GZz/U0Gm1LZwe1G/pttEnEi0Bvf3HasKUVPMvBKlRLja"
	},
	{
		"id": 2,
		"nome": "Stella Ayla Dias",
		"email": "stella.ayla.dias@helpvale.com.br",
		"senha": "$2a$10$5VRkMeIxfNM/kfJ5JSl/x.3isC.Fkkrj4dWyrZa.Is6PwCJ4eXuXq",
		"parceiro": {
			"id": 2,
			"nomeFantasia": "Mercado Livre",
			"dataEntrada": [
				2023,
				9,
				11
			],
			"dataSaida": null,
			"cnpj": "27303425000126"
		},
		"enabled": true,
		"credentialsNonExpired": true,
		"accountNonExpired": true,
		"authorities": [
			{
				"authority": "ROLE_USUARIO"
			}
		],
		"username": "stella.ayla.dias@helpvale.com.br",
		"accountNonLocked": true,
		"password": "$2a$10$5VRkMeIxfNM/kfJ5JSl/x.3isC.Fkkrj4dWyrZa.Is6PwCJ4eXuXq"
	},
	{
		"id": 3,
		"nome": "Lucas Veríssimo",
		"email": "lucas@gmail.com",
		"senha": "$2a$10$k2kBaOBj7s9qtuXxJsEVdungEUxG5elB7T/CzHSfWNkW2iji3a8PK",
		"parceiro": {
			"id": 1,
			"nomeFantasia": "Amazon",
			"dataEntrada": [
				2023,
				9,
				11
			],
			"dataSaida": null,
			"cnpj": "38345431000162"
		},
		"enabled": true,
		"credentialsNonExpired": true,
		"accountNonExpired": true,
		"authorities": [
			{
				"authority": "ROLE_USUARIO"
			}
		],
		"username": "lucas@gmail.com",
		"accountNonLocked": true,
		"password": "$2a$10$k2kBaOBj7s9qtuXxJsEVdungEUxG5elB7T/CzHSfWNkW2iji3a8PK"
	}
]
```

*Resposta*

| código | descrição                             |
| ------ | ------------------------------------- |
| 200    | os dados foram retornados com sucesso |

### Detalhar Funcionário

`GET` /aishoppingbuddy/api/funcionario/{id}

*Exemplo de resposta*
```
{
	"id": 3,
	"nome": "Lucas Veríssimo",
	"email": "lucas@gmail.com",
	"senha": "$2a$10$k2kBaOBj7s9qtuXxJsEVdungEUxG5elB7T/CzHSfWNkW2iji3a8PK",
	"parceiro": {
		"id": 1,
		"nomeFantasia": "Amazon",
		"dataEntrada": [
			2023,
			9,
			11
		],
		"dataSaida": null,
		"cnpj": "38345431000162"
	},
	"enabled": true,
	"credentialsNonExpired": true,
	"accountNonExpired": true,
	"authorities": [
		{
			"authority": "ROLE_USUARIO"
		}
	],
	"username": "lucas@gmail.com",
	"accountNonLocked": true,
	"password": "$2a$10$k2kBaOBj7s9qtuXxJsEVdungEUxG5elB7T/CzHSfWNkW2iji3a8PK"
}
```

*Resposta*

| código | descrição                                      |
| ------ | ---------------------------------------------- |
| 200    | o parceiro foi detalhado com sucesso           |
| 404    | não foi possível achar um parceiro com esse id |

### Apagar Usuário

`DELETE` /aishoppingbuddy/api/funcionario/{id}

*Resposta*

| código | descrição                                         |
| ------ | ------------------------------------------------- |
| 200    | o funcionário foi removido com sucesso            |
| 404    | não foi possível achar um funcionário com esse id |

### Atualizar Usuário

`PUT` /aishoppingbuddy/api/funcionario/{id}

*Campos de requisição*

| campo | tipo   | obrigatório | descrição            |
| ----- | ------ | :---------: | -------------------- |
| nome  | String |     sim     | Nome do Funcionário  |
| email | String |     sim     | Email do Funcionário |
| senha | String |     sim     | Senha do Funcionário |

*Exemplo de requisição*
```
{
  "nome": "Lucas da Silva",
  "email": "lucas_diferente@gmail.com",
  "senha": "senhaDiferente"
}
```

*Resposta*

| código | descrição                                         |
| ------ | ------------------------------------------------- |
| 200    | o funcionário foi atualizado com sucesso          |
| 404    | não foi possível achar um funcionário com esse id |

*Exemplo de resposta*
```
{
	"id": 3,
	"nome": "Lucas da Silva",
	"email": "lucas_diferente@gmail.com",
	"senha": "$2a$10$/EEJKW6dwZnbeDEaHNMEU.yPoVSEE8OGuG.uPtjqaJqIpIAFl0RIG",
	"parceiro": {
		"id": 1,
		"nomeFantasia": "Amazon",
		"dataEntrada": [
			2023,
			9,
			11
		],
		"dataSaida": null,
		"cnpj": "38345431000162"
	},
	"enabled": true,
	"authorities": [
		{
			"authority": "ROLE_USUARIO"
		}
	],
	"username": "lucas_diferente@gmail.com",
	"accountNonExpired": true,
	"credentialsNonExpired": true,
	"accountNonLocked": true,
	"password": "$2a$10$/EEJKW6dwZnbeDEaHNMEU.yPoVSEE8OGuG.uPtjqaJqIpIAFl0RIG"
}
```

## PARCEIRO DE NEGÓCIOS

### Cadastrar Parceiro

`POST` /aishoppingbuddy/api/parceiro

*Campos de requisição*

| campo        | tipo   | obrigatório | descrição                 |
| ------------ | ------ | :---------: | ------------------------- |
| nomeFantasia | String |     sim     | Nome Fantasia do parceiro |
| cnpj         | String |     sim     | CPNJ do parceiro          |

*Exemplo de requisição*
```
{
	"nomeFantasia": "Shopee",
	"cnpj": "95943091000105"
}
```

*Resposta*

| código | descrição                             |
| ------ | ------------------------------------- |
| 201    | o parceiro foi cadastrado com sucesso |
| 400    | dados inválidos                       |

### Listar todos Parceiros

`GET` /aishoppingbuddy/api/parceiro

*Exemplo de resposta*
```
[
	{
		"id": 1,
		"nomeFantasia": "Amazon",
		"dataEntrada": [
			2023,
			9,
			11
		],
		"dataSaida": null,
		"cnpj": "38345431000162"
	},
	{
		"id": 2,
		"nomeFantasia": "Mercado Livre",
		"dataEntrada": [
			2023,
			9,
			11
		],
		"dataSaida": null,
		"cnpj": "27303425000126"
	},
	{
		"id": 3,
		"nomeFantasia": "Shopee",
		"dataEntrada": [
			2023,
			9,
			11
		],
		"dataSaida": null,
		"cnpj": "95943091000105"
	}
]
```

*Resposta*

| código | descrição                             |
| ------ | ------------------------------------- |
| 200    | os dados foram retornados com sucesso |

### Detalhar Parceiro

`GET` /aishoppingbuddy/api/parceiro/{id}

*Exemplo de resposta*
```
{
	"id": 3,
	"nomeFantasia": "Shopee",
	"dataEntrada": [
		2023,
		9,
		11
	],
	"dataSaida": null,
	"cnpj": "95943091000105"
}
```

*Resposta*

| código | descrição                                      |
| ------ | ---------------------------------------------- |
| 200    | o parceiro foi detalhado com sucesso           |
| 404    | não foi possível achar um parceiro com esse id |

### Apagar Parceiro

`DELETE` /aishoppingbuddy/api/parceiro/{id}

*Resposta*

| código | descrição                                      |
| ------ | ---------------------------------------------- |
| 200    | o parceiro foi removido com sucesso            |
| 404    | não foi possível achar um parceiro com esse id |

### Atualizar Parceiro

`PUT` /aishoppingbuddy/api/parceiro/{id}

*Campos de requisição*

| campo            | tipo   | obrigatório | derscrição                       |
| ---------------- | ------ | :---------: | -------------------------------- |
| nomeFantasia     | String |     sim     | Nome Fantasia do parceiro        |
| dataEntrada      | Date   |     sim     | Data de Entrada do parceiro      |
| dataEncerramento | Date   |     sim     | Data de Encerramento do parceiro |
| cnpj             | String |     sim     | CPNJ do parceiro                 |

*Exemplo de requisição*
```
{
	"id": 3,
	"nomeFantasia": "Shopee 2",
	"dataEntrada": [
		2023,
		9,
		11
	],
	"dataSaida": null,
	"cnpj": "49529952000196"
}
```

*Resposta*

| código | descrição                                      |
| ------ | ---------------------------------------------- |
| 200    | o parceiro foi atualizado com sucesso          |
| 404    | não foi possível achar um parceiro com esse id |

## USUÁRIO

### Cadastrar Usuário

`POST` /aishoppingbuddy/api/usuario

*Campos de requisição*

| campo          | tipo   | obrigatório | descrição                     |
| -------------- | ------ | :---------: | ----------------------------- |
| nome           | String |     sim     | Nome do usuário               |
| cpf            | String |     sim     | CPF do usuário                |
| cep            | String |     sim     | CEP do usuário                |
| dataNascimento | Date   |     sim     | Data de nascimento do usuário |
| genero         | String |     sim     | Gênero do usuário             |


*Exemplo de requisição*
```
{
	"nome": "Fátima Elza das Neves",
	"cpf": "25777422152",
	"cep": "79086330",
	"dataNascimento": [1945, 8, 17],
	"genero": "F"
}
```

*Resposta*

| código | descrição                            |
| ------ | ------------------------------------ |
| 201    | o usuário foi cadastrado com sucesso |
| 400    | dados inválidos                      |

### Listar todos Usuários

`GET` /aishoppingbuddy/api/usuario

*Exemplo de resposta*
```
{
	"content": [
		{
			"id": 1,
			"nome": "Sandra Cristiane Sophie Monteiro",
			"cpf": "19265516054",
			"cep": "97543160",
			"dataNascimento": [
				2023,
				9,
				11
			],
			"genero": "F"
		},
		{
			"id": 2,
			"nome": "Mateus Iago Kaique Moreira",
			"cpf": "79528133312",
			"cep": "64000390",
			"dataNascimento": [
				2023,
				9,
				11
			],
			"genero": "M"
		},
		{
			"id": 3,
			"nome": "Pietro Ian Barbosa",
			"cpf": "35789752900",
			"cep": "66913260",
			"dataNascimento": [
				2023,
				9,
				11
			],
			"genero": "M"
		},
		{
			"id": 4,
			"nome": "Sara Julia Nair Barbosa",
			"cpf": "38665570519",
			"cep": "65082585",
			"dataNascimento": [
				2023,
				9,
				11
			],
			"genero": "F"
		},
		{
			"id": 5,
			"nome": "Fátima Elza das Neves",
			"cpf": "25777422152",
			"cep": "79086330",
			"dataNascimento": [
				1945,
				8,
				17
			],
			"genero": "F"
		}
	],
	"pageable": {
		"sort": {
			"empty": true,
			"sorted": false,
			"unsorted": true
		},
		"offset": 0,
		"pageSize": 5,
		"pageNumber": 0,
		"unpaged": false,
		"paged": true
	},
	"last": true,
	"totalElements": 5,
	"totalPages": 1,
	"size": 5,
	"number": 0,
	"sort": {
		"empty": true,
		"sorted": false,
		"unsorted": true
	},
	"first": true,
	"numberOfElements": 5,
	"empty": false
}
```

*Resposta*

| código | descrição                             |
| ------ | ------------------------------------- |
| 200    | os dados foram retornados com sucesso |

### Detalhar Usuário

`GET` /aishoppingbuddy/api/usuario/{id}

*Exemplo de resposta*
```
{
	"id": 5,
	"nome": "Fátima Elza das Neves",
	"cpf": "25777422152",
	"cep": "79086330",
	"dataNascimento": [
		1945,
		8,
		17
	],
	"genero": "F"
}
```

*Resposta*

| código | descrição                                     |
| ------ | --------------------------------------------- |
| 200    | o usuário foi detalhado com sucesso           |
| 404    | não foi possível achar um usuário com esse id |

### Apagar Usuario

`DELETE` /aishoppingbuddy/api/usuario/{id}

*Resposta*

| código | descrição                                     |
| ------ | --------------------------------------------- |
| 200    | o usuário foi removido com sucesso            |
| 404    | não foi possível achar um usuário com esse id |

### Atualizar Usuário

`PUT` /aishoppingbuddy/api/usuario/{id}

*Campos de requisição*

| campo          | tipo   | obrigatório | descrição                     |
| -------------- | ------ | :---------: | ----------------------------- |
| nome           | String |     sim     | Nome do usuário               |
| cpf            | String |     sim     | CPF do usuário                |
| cep            | String |     sim     | CEP do usuário                |
| dataNascimento | Date   |     sim     | Data de nascimento do usuário |
| genero         | String |     sim     | Gênero do usuário             |


*Exemplo de requisição*
```
{
	"nome": "Fábio Elza da Silva",
	"cpf": "45378519101",
	"cep": "75806440",
	"dataNascimento": [1945, 8, 17],
	"genero": "M"
}
```

*Resposta*

| código | descrição                                     |
| ------ | --------------------------------------------- |
| 200    | o usuário foi atualizado com sucesso          |
| 404    | não foi possível achar um usuário com esse id |

### Buscar Usuários por nome

`GET` /aishoppingbuddy/api/usuario/nome/{busca}

*Exemplo de resposta*
```
{
	"content": [
		{
			"id": 1,
			"nome": "Sandra Cristiane Sophie Monteiro",
			"cpf": "19265516054",
			"cep": "97543160",
			"dataNascimento": [
				2023,
				9,
				11
			],
			"genero": "F"
		},
		{
			"id": 3,
			"nome": "Pietro Ian Barbosa",
			"cpf": "35789752900",
			"cep": "66913260",
			"dataNascimento": [
				2023,
				9,
				11
			],
			"genero": "M"
		},
		{
			"id": 4,
			"nome": "Sara Julia Nair Barbosa",
			"cpf": "38665570519",
			"cep": "65082585",
			"dataNascimento": [
				2023,
				9,
				11
			],
			"genero": "F"
		}
	],
	"pageable": {
		"sort": {
			"empty": true,
			"sorted": false,
			"unsorted": true
		},
		"offset": 0,
		"pageNumber": 0,
		"pageSize": 5,
		"paged": true,
		"unpaged": false
	},
	"totalPages": 1,
	"totalElements": 3,
	"last": true,
	"size": 5,
	"number": 0,
	"sort": {
		"empty": true,
		"sorted": false,
		"unsorted": true
	},
	"numberOfElements": 3,
	"first": true,
	"empty": false
}
```

*Resposta*

| código | descrição                             |
| ------ | ------------------------------------- |
| 200    | os dados foram retornados com sucesso |

## PRODUTO

### Cadastrar Produto

`POST` /aishoppingbuddy/api/produto

*Campos de requisição*

| campo     | tipo   | obrigatório | descrição            |
| --------- | ------ | :---------: | -------------------- |
| nome      | String |     sim     | Nome do produto      |
| tipo      | String |     sim     | Tipo do produto      |
| categoria | String |     sim     | Categoria do produto |
| valor     | Float  |     sim     | Preço do produto     |
| descricao | String |     sim     | Descrição do produto |


*Exemplo de requisição*
```
{
  "nome": "Fone de Ouvido Bluetooth",
  "tipo": "Eletrônico",
  "categoria": "Tecnologia",
  "valor": 59.99,
  "descricao": "Um fone de ouvido sem fio com conectividade Bluetooth, cancelamento de ruído e qualidade de áudio excepcional."
}
```

*Resposta*

| código | descrição                            |
| ------ | ------------------------------------ |
| 201    | o produto foi cadastrado com sucesso |
| 400    | dados inválidos                      |

### Listar todos Produtos

`GET` /aishoppingbuddy/api/produto

*Exemplo de resposta*
```
{
	"content": [
		{
			"id": 1,
			"nome": "Smartphone Galaxy S21",
			"tipo": "Eletrônico",
			"valor": 999.99,
			"descricao": "Um smartphone de última geração com tela AMOLED de 6,2 polegadas, câmera de alta resolução e processador poderoso.",
			"categoria": "Tecnologia",
			"parceiro": {
				"id": 1,
				"nomeFantasia": "Amazon",
				"dataEntrada": [
					2023,
					9,
					11
				],
				"dataSaida": null,
				"cnpj": "38345431000162"
			}
		},
		{
			"id": 2,
			"nome": "Livro: O Senhor dos Anéis",
			"tipo": "Livro",
			"valor": 29.99,
			"descricao": "Uma obra épica de fantasia que narra a jornada de Frodo Baggins para destruir o Um Anel e salvar a Terra-média.",
			"categoria": "Literatura",
			"parceiro": {
				"id": 1,
				"nomeFantasia": "Amazon",
				"dataEntrada": [
					2023,
					9,
					11
				],
				"dataSaida": null,
				"cnpj": "38345431000162"
			}
		},
		{
			"id": 5,
			"nome": "Fone de Ouvido Bluetooth",
			"tipo": "Eletrônico",
			"valor": 59.99,
			"descricao": "Um fone de ouvido sem fio com conectividade Bluetooth, cancelamento de ruído e qualidade de áudio excepcional.",
			"categoria": "Tecnologia",
			"parceiro": {
				"id": 1,
				"nomeFantasia": "Amazon",
				"dataEntrada": [
					2023,
					9,
					11
				],
				"dataSaida": null,
				"cnpj": "38345431000162"
			}
		}
	],
	"pageable": {
		"sort": {
			"empty": true,
			"sorted": false,
			"unsorted": true
		},
		"offset": 0,
		"pageNumber": 0,
		"pageSize": 5,
		"paged": true,
		"unpaged": false
	},
	"totalPages": 1,
	"totalElements": 3,
	"last": true,
	"size": 5,
	"number": 0,
	"sort": {
		"empty": true,
		"sorted": false,
		"unsorted": true
	},
	"numberOfElements": 3,
	"first": true,
	"empty": false
}
```

*Resposta*

| código | descrição                             |
| ------ | ------------------------------------- |
| 200    | os dados foram retornados com sucesso |

### Detalhar Produto

`GET` /aishoppingbuddy/api/produto/{id}

*Exemplo de resposta*
```
{
  "id": 5,
  "nome": "Fone de Ouvido Bluetooth",
  "tipo": "Eletrônico",
  "valor": 59.99,
  "descricao": "Um fone de ouvido sem fio com conectividade Bluetooth, cancelamento de ruído e qualidade de áudio excepcional.",
  "categoria": "Tecnologia",
  "parceiro": {
    "id": 1,
    "nomeFantasia": "Amazon",
    "dataEntrada": [
      2023,
      9,
      11
    ],
    "dataSaida": null,
    "cnpj": "38345431000162"
  }
}
```

*Resposta*

| código | descrição                                     |
| ------ | --------------------------------------------- |
| 200    | o produto foi detalhado com sucesso           |
| 404    | não foi possível achar um produto com esse id |

### Apagar Produto

`DELETE` /aishoppingbuddy/api/produto/{id}

*Resposta*

| código | descrição                                     |
| ------ | --------------------------------------------- |
| 200    | o produto foi removido com sucesso            |
| 404    | não foi possível achar um produto com esse id |

### Atualizar Produto

`PUT` /aishoppingbuddy/api/produto/{id}

*Campos de requisição*

| campo     | tipo   | obrigatório | descrição            |
| --------- | ------ | :---------: | -------------------- |
| nome      | String |     sim     | Nome do produto      |
| tipo      | String |     sim     | Tipo do produto      |
| categoria | String |     sim     | Categoria do produto |
| valor     | Float  |     sim     | Preço do produto     |
| descricao | String |     sim     | Descrição do produto |


*Exemplo de requisição*
```
{
  "nome": "Fone de Ouvido 2 Bluetooth com fio",
  "tipo": "Eletrônico 2",
  "categoria": "Tecnologia 2",
  "valor": 5999.99,
  "descricao": "Um fone de ouvido sem fio com fio com conectividade Bluetooth co fio, aumento de ruído e qualidade de áudio péssima."
}
```

*Resposta*

| código | descrição                                     |
| ------ | --------------------------------------------- |
| 200    | o produto foi atualizado com sucesso          |
| 404    | não foi possível achar um produto com esse id |

### Buscar Produtos por nome

`GET` /aishoppingbuddy/api/produto/nome/{busca}

*Exemplo de resposta*
```
{
	"content": [
		{
			"id": 1,
			"nome": "Smartphone Galaxy S21",
			"tipo": "Eletrônico",
			"valor": 999.99,
			"descricao": "Um smartphone de última geração com tela AMOLED de 6,2 polegadas, câmera de alta resolução e processador poderoso.",
			"categoria": "Tecnologia",
			"parceiro": {
				"id": 1,
				"nomeFantasia": "Amazon",
				"dataEntrada": [
					2023,
					9,
					11
				],
				"dataSaida": null,
				"cnpj": "38345431000162"
			}
		},
		{
			"id": 5,
			"nome": "Fone de Ouvido 2 Bluetooth com fio",
			"tipo": "Eletrônico 2",
			"valor": 5999.99,
			"descricao": "Um fone de ouvido sem fio com fio com conectividade Bluetooth co fio, aumento de ruído e qualidade de áudio péssima.",
			"categoria": "Tecnologia 2",
			"parceiro": {
				"id": 1,
				"nomeFantasia": "Amazon",
				"dataEntrada": [
					2023,
					9,
					11
				],
				"dataSaida": null,
				"cnpj": "38345431000162"
			}
		}
	],
	"pageable": {
		"sort": {
			"empty": true,
			"sorted": false,
			"unsorted": true
		},
		"offset": 0,
		"pageNumber": 0,
		"pageSize": 5,
		"paged": true,
		"unpaged": false
	},
	"totalPages": 1,
	"totalElements": 2,
	"last": true,
	"size": 5,
	"number": 0,
	"sort": {
		"empty": true,
		"sorted": false,
		"unsorted": true
	},
	"numberOfElements": 2,
	"first": true,
	"empty": false
}
```

*Resposta*

| código | descrição                             |
| ------ | ------------------------------------- |
| 200    | os dados foram retornados com sucesso |

## RECOMENDAÇÃO

### Gerar Recomendação

`POST` /aishoppingbuddy/api/recomendacao/{idUsuario}

*Campos de requisição*

| campo       | tipo          | obrigatório | descrição                                |
| ----------- | ------------- | :---------: | ---------------------------------------- |
| produtoList | List<Produto> |     sim     | Lista de Produtos que serão recomendados |


*Exemplo de requisição*
```
{
	"produtoList":[
		{
			"id": 2
		},
		{
			"id": 5
		}
	]
}
```

*Resposta*

| código | descrição                             |
| ------ | ------------------------------------- |
| 201    | o recomendação foi criada com sucesso |
| 400    | dados inválidos                       |

### Listar todos Recomendações

`GET` /aishoppingbuddy/api/recomendacao

*Exemplo de resposta*
```
{
	"content": [
		{
			"id": 2,
			"titulo": "Livro: O Senhor dos Anéis",
			"mensagem": "MENSAGEM GERADA PELO CHATGPT",
			"data": [
				2023,
				9,
				11
			],
			"produtoList": [
				{
					"id": 2,
					"nome": "Livro: O Senhor dos Anéis",
					"tipo": "Livro",
					"valor": 29.99,
					"descricao": "Uma obra épica de fantasia que narra a jornada de Frodo Baggins para destruir o Um Anel e salvar a Terra-média.",
					"categoria": "Literatura",
					"parceiro": {
						"id": 1,
						"nomeFantasia": "Amazon",
						"dataEntrada": [
							2023,
							9,
							11
						],
						"dataSaida": null,
						"cnpj": "38345431000162"
					}
				},
				{
					"id": 5,
					"nome": "Fone de Ouvido Bluetooth",
					"tipo": "Eletrônico",
					"valor": 59.99,
					"descricao": "Um fone de ouvido sem fio com conectividade Bluetooth, cancelamento de ruído e qualidade de áudio excepcional.",
					"categoria": "Tecnologia",
					"parceiro": {
						"id": 1,
						"nomeFantasia": "Amazon",
						"dataEntrada": [
							2023,
							9,
							11
						],
						"dataSaida": null,
						"cnpj": "38345431000162"
					}
				}
			],
			"usuario": {
				"id": 1,
				"nome": "Sandra Cristiane Sophie Monteiro",
				"cpf": "19265516054",
				"cep": "97543160",
				"dataNascimento": [
					2023,
					9,
					11
				],
				"genero": "F"
			},
			"parceiro": {
				"id": 1,
				"nomeFantasia": "Amazon",
				"dataEntrada": [
					2023,
					9,
					11
				],
				"dataSaida": null,
				"cnpj": "38345431000162"
			}
		},
		{
			"id": 3,
			"titulo": "Livro: O Senhor dos Anéis",
			"mensagem": "MENSAGEM GERADA PELO CHATGPT",
			"data": [
				2023,
				9,
				11
			],
			"produtoList": [
				{
					"id": 2,
					"nome": "Livro: O Senhor dos Anéis",
					"tipo": "Livro",
					"valor": 29.99,
					"descricao": "Uma obra épica de fantasia que narra a jornada de Frodo Baggins para destruir o Um Anel e salvar a Terra-média.",
					"categoria": "Literatura",
					"parceiro": {
						"id": 1,
						"nomeFantasia": "Amazon",
						"dataEntrada": [
							2023,
							9,
							11
						],
						"dataSaida": null,
						"cnpj": "38345431000162"
					}
				},
				{
					"id": 5,
					"nome": "Fone de Ouvido Bluetooth",
					"tipo": "Eletrônico",
					"valor": 59.99,
					"descricao": "Um fone de ouvido sem fio com conectividade Bluetooth, cancelamento de ruído e qualidade de áudio excepcional.",
					"categoria": "Tecnologia",
					"parceiro": {
						"id": 1,
						"nomeFantasia": "Amazon",
						"dataEntrada": [
							2023,
							9,
							11
						],
						"dataSaida": null,
						"cnpj": "38345431000162"
					}
				}
			],
			"usuario": {
				"id": 2,
				"nome": "Mateus Iago Kaique Moreira",
				"cpf": "79528133312",
				"cep": "64000390",
				"dataNascimento": [
					2023,
					9,
					11
				],
				"genero": "M"
			},
			"parceiro": {
				"id": 1,
				"nomeFantasia": "Amazon",
				"dataEntrada": [
					2023,
					9,
					11
				],
				"dataSaida": null,
				"cnpj": "38345431000162"
			}
		},
		{
			"id": 4,
			"titulo": "Smartphone Galaxy S21",
			"mensagem": "MENSAGEM GERADA PELO CHATGPT",
			"data": [
				2023,
				9,
				11
			],
			"produtoList": [
				{
					"id": 1,
					"nome": "Smartphone Galaxy S21",
					"tipo": "Eletrônico",
					"valor": 999.99,
					"descricao": "Um smartphone de última geração com tela AMOLED de 6,2 polegadas, câmera de alta resolução e processador poderoso.",
					"categoria": "Tecnologia",
					"parceiro": {
						"id": 1,
						"nomeFantasia": "Amazon",
						"dataEntrada": [
							2023,
							9,
							11
						],
						"dataSaida": null,
						"cnpj": "38345431000162"
					}
				},
				{
					"id": 5,
					"nome": "Fone de Ouvido Bluetooth",
					"tipo": "Eletrônico",
					"valor": 59.99,
					"descricao": "Um fone de ouvido sem fio com conectividade Bluetooth, cancelamento de ruído e qualidade de áudio excepcional.",
					"categoria": "Tecnologia",
					"parceiro": {
						"id": 1,
						"nomeFantasia": "Amazon",
						"dataEntrada": [
							2023,
							9,
							11
						],
						"dataSaida": null,
						"cnpj": "38345431000162"
					}
				}
			],
			"usuario": {
				"id": 2,
				"nome": "Mateus Iago Kaique Moreira",
				"cpf": "79528133312",
				"cep": "64000390",
				"dataNascimento": [
					2023,
					9,
					11
				],
				"genero": "M"
			},
			"parceiro": {
				"id": 1,
				"nomeFantasia": "Amazon",
				"dataEntrada": [
					2023,
					9,
					11
				],
				"dataSaida": null,
				"cnpj": "38345431000162"
			}
		}
	],
	"pageable": {
		"sort": {
			"empty": true,
			"sorted": false,
			"unsorted": true
		},
		"offset": 0,
		"pageSize": 5,
		"pageNumber": 0,
		"paged": true,
		"unpaged": false
	},
	"last": true,
	"totalPages": 1,
	"totalElements": 3,
	"size": 5,
	"number": 0,
	"sort": {
		"empty": true,
		"sorted": false,
		"unsorted": true
	},
	"first": true,
	"numberOfElements": 3,
	"empty": false
}
```

*Resposta*

| código | descrição                             |
| ------ | ------------------------------------- |
| 200    | os dados foram retornados com sucesso |

### Buscar Recomendações por título

`GET` /aishoppingbuddy/api/recomendacao/busca/{busca}

*Exemplo de resposta*
```
{
	"content": [
		{
			"id": 2,
			"titulo": "Livro: O Senhor dos Anéis",
			"mensagem": "MENSAGEM GERADA PELO CHATGPT",
			"data": [
				2023,
				9,
				11
			],
			"produtoList": [
				{
					"id": 2,
					"nome": "Livro: O Senhor dos Anéis",
					"tipo": "Livro",
					"valor": 29.99,
					"descricao": "Uma obra épica de fantasia que narra a jornada de Frodo Baggins para destruir o Um Anel e salvar a Terra-média.",
					"categoria": "Literatura",
					"parceiro": {
						"id": 1,
						"nomeFantasia": "Amazon",
						"dataEntrada": [
							2023,
							9,
							11
						],
						"dataSaida": null,
						"cnpj": "38345431000162"
					}
				},
				{
					"id": 5,
					"nome": "Fone de Ouvido Bluetooth",
					"tipo": "Eletrônico",
					"valor": 59.99,
					"descricao": "Um fone de ouvido sem fio com conectividade Bluetooth, cancelamento de ruído e qualidade de áudio excepcional.",
					"categoria": "Tecnologia",
					"parceiro": {
						"id": 1,
						"nomeFantasia": "Amazon",
						"dataEntrada": [
							2023,
							9,
							11
						],
						"dataSaida": null,
						"cnpj": "38345431000162"
					}
				}
			],
			"usuario": {
				"id": 1,
				"nome": "Sandra Cristiane Sophie Monteiro",
				"cpf": "19265516054",
				"cep": "97543160",
				"dataNascimento": [
					2023,
					9,
					11
				],
				"genero": "F"
			},
			"parceiro": {
				"id": 1,
				"nomeFantasia": "Amazon",
				"dataEntrada": [
					2023,
					9,
					11
				],
				"dataSaida": null,
				"cnpj": "38345431000162"
			}
		},
		{
			"id": 3,
			"titulo": "Livro: O Senhor dos Anéis",
			"mensagem": "MENSAGEM GERADA PELO CHATGPT",
			"data": [
				2023,
				9,
				11
			],
			"produtoList": [
				{
					"id": 2,
					"nome": "Livro: O Senhor dos Anéis",
					"tipo": "Livro",
					"valor": 29.99,
					"descricao": "Uma obra épica de fantasia que narra a jornada de Frodo Baggins para destruir o Um Anel e salvar a Terra-média.",
					"categoria": "Literatura",
					"parceiro": {
						"id": 1,
						"nomeFantasia": "Amazon",
						"dataEntrada": [
							2023,
							9,
							11
						],
						"dataSaida": null,
						"cnpj": "38345431000162"
					}
				},
				{
					"id": 5,
					"nome": "Fone de Ouvido Bluetooth",
					"tipo": "Eletrônico",
					"valor": 59.99,
					"descricao": "Um fone de ouvido sem fio com conectividade Bluetooth, cancelamento de ruído e qualidade de áudio excepcional.",
					"categoria": "Tecnologia",
					"parceiro": {
						"id": 1,
						"nomeFantasia": "Amazon",
						"dataEntrada": [
							2023,
							9,
							11
						],
						"dataSaida": null,
						"cnpj": "38345431000162"
					}
				}
			],
			"usuario": {
				"id": 2,
				"nome": "Mateus Iago Kaique Moreira",
				"cpf": "79528133312",
				"cep": "64000390",
				"dataNascimento": [
					2023,
					9,
					11
				],
				"genero": "M"
			},
			"parceiro": {
				"id": 1,
				"nomeFantasia": "Amazon",
				"dataEntrada": [
					2023,
					9,
					11
				],
				"dataSaida": null,
				"cnpj": "38345431000162"
			}
		}
	],
	"pageable": {
		"sort": {
			"empty": true,
			"sorted": false,
			"unsorted": true
		},
		"offset": 0,
		"pageSize": 5,
		"pageNumber": 0,
		"paged": true,
		"unpaged": false
	},
	"last": true,
	"totalPages": 1,
	"totalElements": 2,
	"size": 5,
	"number": 0,
	"sort": {
		"empty": true,
		"sorted": false,
		"unsorted": true
	},
	"first": true,
	"numberOfElements": 2,
	"empty": false
}
```

*Resposta*

| código | descrição                             |
| ------ | ------------------------------------- |
| 200    | os dados foram retornados com sucesso |