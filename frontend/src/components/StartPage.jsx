import React, { useState, useRef, useEffect } from "react";
import "./StartPage.css";
import Sidebar from "./SideBar";

const StartPage = () => {
  const [message, setMessage] = useState("");
  const [file, setFile] = useState(null);
  const [isSidebarOpen, setSidebarOpen] = useState(true); 
  const [chats, setChats] = useState([
    { id: 1, title: "Новый чат", messages: [] },
  ]);
  const [activeChatId, setActiveChatId] = useState(1);
  const textareaRef = useRef(null);
  const messagesEndRef = useRef(null);
  const [role, setRole] = useState(null);


  const replies = [
    "Сәлем, досым!",
    "Қалайсың?",
    "Мынаны түсінбедім, қайта жазшы!",
    "Өте қызық сұрақ екен!",
  ];

  const toggleS = () => {
    setSidebarOpen(!isSidebarOpen);
  };

  const handleSend = () => {
    if (message.trim() === "" && !file) return;
  
    // Добавляем сообщение пользователя в чат
    const newMessage = { text: message, sender: "user", file };
  
    // Выбираем случайный ответ бота
    const randomReply = replies[Math.floor(Math.random() * replies.length)];
  
    // Ответ бота с кнопкой квиза
    const botMessageWithQuizButton = {
      text: randomReply,
      sender: "bot",
      showQuizButton: true,
    };
  
    // Обновляем чат
    setChats((prevChats) =>
      prevChats.map((chat) =>
        chat.id === activeChatId
          ? {
              ...chat,
              messages: [...chat.messages, newMessage, botMessageWithQuizButton],
            }
          : chat
      )
    );
  
    // Очищаем состояние
    setMessage("");
    setFile(null);
    document.getElementById("fileInput").value = null;
  };
  

  const handleQuiz = (topic) => {
    const shortTopic = topic.length > 50 ? topic.slice(0, 50) + "..." : topic;

const quizMessage = {
  text: `📝 Квиз по теме: *${shortTopic}*

    1️⃣ Что означает HTML?
    - A) HyperText Markup Language
    - B) HighText Machine Language
    - C) HyperTool Multi Language

    2️⃣ Какой тег используется для создания ссылки?
    - A) <link>
    - B) <a>
    - C) <href>

    3️⃣ Как правильно подключить CSS к HTML?
    - A) <style src="style.css">
    - B) <link rel="stylesheet" href="style.css">
    - C) <css link="style.css">`,
      sender: "bot",
      showAnswersButton: true // флаг, чтобы отобразить кнопку
    };



  
    setChats((prevChats) =>
      prevChats.map((chat) =>
        chat.id === activeChatId
          ? { ...chat, messages: [...chat.messages, quizMessage] }
          : chat
      )
    );
  };
  
  


  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const removeFile = () => {
    setFile(null);
    document.getElementById("fileInput").value = null;
  };

  const handleRoleSelect = (selectedRole) => {
    setRole(selectedRole);
  };
  
  

  const handleNewChat = () => {
    const newId = Date.now();
    setChats((prev) => [
      ...prev,
      { id: newId, title: "Новый чат", messages: [] },
    ]);
    setActiveChatId(newId);
    setMessage("");
    setFile(null);
  };

  const handleSelectChat = (id) => {
    setActiveChatId(id);
    setMessage("");
    setFile(null);
  };

  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [chats, activeChatId]);

  useEffect(() => {
    if (textareaRef.current) {
      textareaRef.current.style.height = "auto";
      textareaRef.current.style.height =
        Math.min(textareaRef.current.scrollHeight, 200) + "px";
    }
  }, [message]);
  const handleShowAnswers = () => {
    const answersMessage = {
      text: `✅ Правильные ответы:
  
  1️⃣ A) HyperText Markup Language  
  2️⃣ B) <a>  
  3️⃣ B) <link rel="stylesheet" href="style.css">`,
      sender: "bot",
    };
  
    setChats((prevChats) =>
      prevChats.map((chat) =>
        chat.id === activeChatId
          ? { ...chat, messages: [...chat.messages, answersMessage] }
          : chat
      )
    );
  };
  

  const activeChat = chats.find((c) => c.id === activeChatId);

  return (
    <div className="main">
      <div className="start-page">
      <button className="burger-btn" onClick={toggleS}>
        ☰
      </button>

      <Sidebar
        isOpen={isSidebarOpen}
        onClose={toggleS}
        chats={chats}
        onSelectChat={handleSelectChat}
        onNewChat={handleNewChat}
      />

      <h1 className="start-title">qysqasha zhazayin ba?</h1>

      <div className="messages">
        {activeChat.messages.map((msg, index) => (
          <div key={index} className={`message ${msg.sender}`}>
            <div>{msg.text}</div>
            {msg.showQuizButton && (
              <button
                className="quiz-button"
                onClick={() => handleQuiz(msg.text)}
              >
                Сделать квиз
              </button>
            )}
            {msg.showAnswersButton && (
              <button
                className="answers-button"
                onClick={handleShowAnswers}
              >
                Посмотреть ответы
              </button>
            )}


            {msg.file && (
              <div className="file-in-message">
                {msg.file.type.startsWith("image/") ? (
                  <img
                    src={URL.createObjectURL(msg.file)}
                    alt="attached"
                    className="attached-image"
                  />
                ) : (
                  <a
                    href={URL.createObjectURL(msg.file)}
                    download={msg.file.name}
                    className="file-download-link"
                  >
                    📎 {msg.file.name}
                  </a>
                )}
              </div>
            )}
          </div>
        ))}
        <div ref={messagesEndRef}></div>
      </div>

      
      <div className="input-container chatgpt-style">
        <textarea
          ref={textareaRef}
          placeholder="Спросите что-нибудь..."
          className="start-input"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter" && (e.ctrlKey || e.metaKey)) {
              e.preventDefault();
              handleSend();
            }
          }}
          rows={1}
        />
        {file && (
        <div className="file-preview">
          {file.type.startsWith("image/") ? (
            <img
              src={URL.createObjectURL(file)}
              alt="preview"
              className="preview-image"
            />
          ) : (
            <p className="file-name">{file.name}</p>
          )}
          <button className="remove-file-button" onClick={removeFile} style={{marginLeft:'5px'}}>
            х
          </button>
        </div>
      )}
        
        <label htmlFor="fileInput" className="icon-button">
          📎
        </label>
        <input
          id="fileInput"
          type="file"
          className="file-input"
          onChange={handleFileChange}
        />

        <button className="icon-button" onClick={handleSend}>
          📤
        </button>
      </div>
      <div className="role-buttons">
        <button
          className={`role-button ${role === "ученик" ? "selected" : ""}`}
          onClick={() => handleRoleSelect("ученик")}
        >
          Я ученик
        </button>
        <button
          className={`role-button ${role === "учитель" ? "selected" : ""}`}
          onClick={() => handleRoleSelect("учитель")}
        >
          Я учитель
        </button>
      </div>



      
    </div>
    </div>
  );
};

export default StartPage;
