#include <winsock2.h>
#include <iostream>

#pragma comment(lib, "ws2_32.lib")

int main() {
    WSADATA wsa;
    WSAStartup(MAKEWORD(2,2), &wsa);

    SOCKET sock = socket(AF_INET, SOCK_STREAM, 0);

    sockaddr_in server{};
    server.sin_family = AF_INET;
    server.sin_port = htons(8080);
    server.sin_addr.s_addr = inet_addr("127.0.0.1");

    connect(sock, (sockaddr*)&server, sizeof(server));

    const char* request =
        "GET /send?msg=Hello_from_client_cpp HTTP/1.1\r\n"
        "Host: localhost\r\n\r\n";

    send(sock, request, strlen(request), 0);

    char buffer[2048]{};
    recv(sock, buffer, sizeof(buffer), 0);

    std::cout << "Server reply:\n" << buffer << std::endl;

    closesocket(sock);
    WSACleanup();
}
