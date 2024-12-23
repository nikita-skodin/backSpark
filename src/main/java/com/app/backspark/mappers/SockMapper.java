package com.app.backspark.mappers;

import com.app.backspark.models.Sock;
import com.app.backspark.views.SockView;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SockMapper {
    private final ModelMapper modelMapper;

    public SockView toView(Sock sock) {
        return modelMapper.map(sock, SockView.class);
    }

    public Sock toEntity(Sock sock, SockView sockView) {
        modelMapper.map(sockView, sock);
        return sock;
    }
}
