#include <iostream>
#include <vector>
#include <thread>
#include <winsock2.h>
#pragma comment(lib, "ws2_32.lib")

#define PORT 8080
#define BUF_SIZE 1024

std::vector<SOCKET> clients;

void handleClient(SOCKET client) {
    char buffer[BUF_SIZE];

    while (true) {
        int len = recv(client, buffer, BUF_SIZE, 0);
        if (len <= 0) break;

        buffer[len] = '\0';
        std::cout << "Client: " << buffer << std::endl;

        // Broadcast cho tất cả client khác
        for (SOCKET c : clients) {
            if (c != client) {
                send(c, buffer, len, 0);
            }
        }
    }

    closesocket(client);
}

int main() {
    WSADATA wsa;
    SOCKET server, client;
    sockaddr_in addr, clientAddr;
    int clientSize = sizeof(clientAddr);

    WSAStartup(MAKEWORD(2,2), &wsa);

    server = socket(AF_INET, SOCK_STREAM, 0);
    addr.sin_family = AF_INET;
    addr.sin_port = htons(PORT);
    addr.sin_addr.s_addr = INADDR_ANY;

    bind(server, (sockaddr*)&addr, sizeof(addr));
    listen(server, 5);

    std::cout << "Chat Server running on port " << PORT << "...\n";

    while (true) {
        client = accept(server, (sockaddr*)&clientAddr, &clientSize);
        clients.push_back(client);

        std::thread(handleClient, client).detach();
    }

    closesocket(server);
    WSACleanup();
    return 0;
}
