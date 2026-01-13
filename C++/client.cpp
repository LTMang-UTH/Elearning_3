#include <iostream>
#include <thread>
#include <winsock2.h>
#pragma comment(lib, "ws2_32.lib")

#define PORT 8080
#define BUF_SIZE 1024

void receiveMsg(SOCKET client) {
    char buffer[BUF_SIZE];
    while (true) {
        int len = recv(client, buffer, BUF_SIZE, 0);
        if (len > 0) {
            buffer[len] = '\0';
            std::cout << "\n>> " << buffer << "\nYou: ";
        }
    }
}

int main() {
    WSADATA wsa;
    SOCKET client;
    sockaddr_in serverAddr;
    char buffer[BUF_SIZE];

    WSAStartup(MAKEWORD(2,2), &wsa);

    client = socket(AF_INET, SOCK_STREAM, 0);
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(PORT);
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");

    connect(client, (sockaddr*)&serverAddr, sizeof(serverAddr));

    std::thread(receiveMsg, client).detach();

    while (true) {
        std::cout << "You: ";
        std::cin.getline(buffer, BUF_SIZE);
        send(client, buffer, strlen(buffer), 0);
    }

    closesocket(client);
    WSACleanup();
    return 0;
}
