import React from "react";
import "./SideBar.css";

const Sidebar = ({ isOpen, onClose, chats, onSelectChat, onNewChat }) => {
  return (
    <div className={`sidebar ${isOpen ? "open" : ""}`}>
      <button className="close-btn" onClick={onClose}></button>
      <h2 className="sidebar-title">История</h2>
      <button className="new-chat-btn" onClick={onNewChat}>➕ Новый чат</button>
      <ul className="history-list">
        {chats.map((chat) => (
          <li key={chat.id} onClick={() => onSelectChat(chat.id)}>
            📝 {chat.messages[0]?.text.slice(0, 30) || "Новый чат"}
          </li>
        ))}
      </ul>
    </div>
  );
};


export default Sidebar;
