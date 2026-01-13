#include <winsock2.h>
#include <ws2tcpip.h>
#include <iostream>
#include <string>
#include <sstream>

#pragma comment(lib, "ws2_32.lib")

#define PORT 8080
#define BUFFER_SIZE 4096

// Hàm decode URL (%20, %C3%A8...)
std::string urlDecode(const std::string& str) {
    std::string result;
    char ch;
    int i, ii;

    for (i = 0; i < str.length(); i++) {
        if (str[i] == '%') {
            sscanf(str.substr(i + 1, 2).c_str(), "%x", &ii);
            ch = static_cast<char>(ii);
            result += ch;
            i += 2;
        }
        else if (str[i] == '+') {
            result += ' ';
        }
        else {
            result += str[i];
        }
    }
    return result;
}

int main() {
    WSADATA wsa;
    SOCKET serverSocket, clientSocket;
    sockaddr_in serverAddr{}, clientAddr{};
    int clientSize = sizeof(clientAddr);
    char buffer[BUFFER_SIZE];

    WSAStartup(MAKEWORD(2, 2), &wsa);

    serverSocket = socket(AF_INET, SOCK_STREAM, 0);

    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(PORT);
    serverAddr.sin_addr.s_addr = INADDR_ANY;

    bind(serverSocket, (sockaddr*)&serverAddr, sizeof(serverAddr));
    listen(serverSocket, 5);

    std::cout << "Chat Server running on port " << PORT << "...\n";

    while (true) {
        clientSocket = accept(serverSocket, (sockaddr*)&clientAddr, &clientSize);

        int bytes = recv(clientSocket, buffer, BUFFER_SIZE - 1, 0);
        if (bytes <= 0) continue;

        buffer[bytes] = '\0';
        std::string request(buffer);

        std::cout << "Client request:\n" << request << "\n";

        // Lấy msg từ URL: ?msg=....
        std::string msg = "";
        size_t pos = request.find("msg=");
        if (pos != std::string::npos) {
            msg = request.substr(pos + 4);
            size_t end = msg.find(" ");
            if (end != std::string::npos)
                msg = msg.substr(0, end);

            msg = urlDecode(msg);
        }

        std::cout << "Decoded message: " << msg << "\n";

        // Trả HTML về trình duyệt
        std::string response =
            "HTTP/1.1 200 OK\r\n"
            "Content-Type: text/plain; charset=UTF-8\r\n"
            "Access-Control-Allow-Origin: *\r\n\r\n"
            "Server: " + msg;

        send(clientSocket, response.c_str(), response.length(), 0);
        closesocket(clientSocket);
    }

    closesocket(serverSocket);
    WSACleanup();
    return 0;
}
