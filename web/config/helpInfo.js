petroresConfig.helpInfo = [
  {
    text:"О Приложении Автоматизированная система хранения и доступа к документам",
    url:"docs/1.html",
    id:"page-1",
    leaf: true
  },
  {
    text:"Начало работы",
    url:"docs/2.html",
	id:"page-2",
	leaf: true
  },
  {
    text:"Меню Documents",
    url:"docs/3.html",
    id:"page-3",
    children:[
	    {
          text:"Команда Register",
          url:"docs/3.1.html",
          id:"page-3.1",
          children:[{
                text:"Привязка к пространственным объектам",
                url:"docs/3.1.1.html",
                id:"page-3.1.1",
                leaf: true
              }, {
                text:"Регистрация каталога файлов",
                url:"docs/3.1.2.html",
                id:"page-3.1.2",
                leaf: true
              }]
        },
		{
          text:"Команда Find",
          url:"docs/3.2.html",
          id:"page-3.2",
          children:[{
                text:"Простой поиск",
                url:"docs/3.2.1.html",
                id:"page-3.2.1",
                leaf: true
              },
			  {
                text:"Расширенный поиск",
                url:"docs/3.2.2.html",
                id:"page-3.2.2",
                leaf: true
              }]
        },
		{
          text:"Команда Documents Catalog",
          url:"docs/3.3.html",
          id:"page-3.3",
		  leaf: true
		}
    ]
  },
  {
    text:"Меню Map",
    url:"docs/4.html",
    id:"page-4",
    leaf: true
  },
  {
    text:"Меню Options",
    url:"docs/5.html",
    id:"page-5",
    leaf: true
  }
];