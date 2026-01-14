package main

import (
	"log"
	"net/http"
	"sync"
	"time"

	"github.com/gorilla/websocket"
)

type Client struct {
	conn     *websocket.Conn
	username string
}

type Message struct {
	Type     string `json:"type"`
	Username string `json:"username,omitempty"`
	Content  string `json:"content,omitempty"`
	Count    int    `json:"count,omitempty"`
	Time     string `json:"time,omitempty"`
}

var (
	clients   = make(map[*Client]bool)
	mutex     sync.Mutex
	broadcast = make(chan Message)
	upgrader  = websocket.Upgrader{
		CheckOrigin: func(r *http.Request) bool { return true },
	}
)

func handleWS(w http.ResponseWriter, r *http.Request) {
	conn, _ := upgrader.Upgrade(w, r, nil)
	client := &Client{conn: conn}

	mutex.Lock()
	clients[client] = true
	mutex.Unlock()

	for {
		var msg Message
		err := conn.ReadJSON(&msg)
		if err != nil {
			handleDisconnect(client)
			return
		}

		// Lần đầu gửi username
		if msg.Type == "join" {
			client.username = msg.Username
			broadcast <- Message{
				Type:     "join",
				Username: client.username,
				Time:     now(),
			}
			sendCount()
			continue
		}

		// Chat
		if msg.Type == "chat" {
			msg.Time = now()
			broadcast <- msg
		}
	}
}

func handleDisconnect(client *Client) {
	mutex.Lock()
	delete(clients, client)
	mutex.Unlock()

	if client.username != "" {
		broadcast <- Message{
			Type:     "leave",
			Username: client.username,
			Time:     now(),
		}
		sendCount()
	}
	client.conn.Close()
}

func sendCount() {
	mutex.Lock()
	count := len(clients)
	mutex.Unlock()

	broadcast <- Message{
		Type:  "count",
		Count: count,
	}
}

func handleBroadcast() {
	for {
		msg := <-broadcast
		mutex.Lock()
		for c := range clients {
			c.conn.WriteJSON(msg)
		}
		mutex.Unlock()
	}
}

func now() string {
	return time.Now().Format("15:04:05")
}

func main() {
	http.HandleFunc("/ws", handleWS)
	go handleBroadcast()
	log.Println("🚀 Go WebSocket Server chạy tại :8080")
	http.ListenAndServe(":8080", nil)
}
