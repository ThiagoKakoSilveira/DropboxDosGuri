package com.technocorp.DropboxDosGuri.armazenamento;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface ServicosArmazenamento {

    void armazena(MultipartFile arquivo, String nomeUsuario);

    Stream<Path> carregaTodos(String nomeUsuario);

    Path carrega(String nomeArquivo);

    Resource carregaRecurso(String nomeArquivo, String nomeUsuario);

    void exclui(String nomeArquivo, String nomeUsuario);
}
