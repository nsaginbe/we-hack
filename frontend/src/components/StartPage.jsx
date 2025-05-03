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

    const newMessage = { text: message, sender: "user" };

    setChats((prevChats) =>
      prevChats.map((chat) =>
        chat.id === activeChatId
          ? { ...chat, messages: [...chat.messages, newMessage] }
          : chat
      )
    );

    // Ответ бота
    setTimeout(() => {
      const randomReply = replies[Math.floor(Math.random() * replies.length)];
      const botResponse = { text: randomReply, sender: "bot" };

      setChats((prevChats) =>
        prevChats.map((chat) =>
          chat.id === activeChatId
            ? { ...chat, messages: [...chat.messages, botResponse] }
            : chat
        )
      );
    }, 1000);

    setMessage("");
    setFile(null);
    document.getElementById("fileInput").value = null;
  };

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const removeFile = () => {
    setFile(null);
    document.getElementById("fileInput").value = null;
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

  const activeChat = chats.find((c) => c.id === activeChatId);

  return (
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
            {msg.text}
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
          <button className="remove-file-button" onClick={removeFile}>
            х
          </button>
        </div>
      )}
    </div>
  );
};

export default StartPage;
