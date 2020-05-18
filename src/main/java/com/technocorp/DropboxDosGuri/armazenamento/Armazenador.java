package com.technocorp.DropboxDosGuri.armazenamento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class Armazenador implements ServicosArmazenamento{

    private final Path caminhoRaiz;
    private String caminhoDoUsuario;

    @Autowired
    public Armazenador(ArmazenamentoProperties caminhoRaiz) {
        this.caminhoRaiz = Paths.get(caminhoRaiz.getLocal());
    }

    @Override
    public void armazena(MultipartFile arquivo, String nomeUsuario) {
        String nomeArquivo = StringUtils.cleanPath(arquivo.getOriginalFilename());
        try {
            if (arquivo.isEmpty()) {
                throw new ExcecaoArmazenamento("Falha, Arquivo vazio! " + nomeArquivo);
            }
            if (nomeArquivo.contains("..")) {
                // This is a security check
                throw new ExcecaoArmazenamento(
                        "Arquivo com nome inválido! Remova os pontos! "
                                + nomeArquivo);
            }
            try (InputStream inputStream = arquivo.getInputStream()) {
                if(!Files.exists(this.caminhoRaiz) || !Files.exists(this.caminhoRaiz.resolve(nomeUsuario))){
                    Files.createDirectories(caminhoRaiz.resolve(nomeUsuario));
                }
                Files.copy(inputStream, this.caminhoRaiz.resolve(nomeUsuario + "/" + nomeArquivo),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new ExcecaoArmazenamento("Erro ao armazenar o arquivo " + nomeArquivo, e);
        }
    }

    @Override
    public Stream<Path> carregaTodos(String nomeUsuario) {
        try {
            return Files.walk(this.caminhoRaiz.resolve(nomeUsuario), 1)
                    .filter(path -> !path.equals(this.caminhoRaiz.resolve(nomeUsuario)))
                    .map(this.caminhoRaiz.resolve(nomeUsuario)::relativize);
        }
        catch (IOException e) {
            throw new ExcecaoArmazenamento("Erro ao ler o arquivo armazenado! ", e);
        }
    }

    @Override
    public Path carrega(String nomeArquivo) {
        return caminhoRaiz.resolve(nomeArquivo);
    }

    @Override
    public Resource carregaRecurso(String nomeArquivo, String nomeUsuario) {
        this.caminhoDoUsuario = nomeUsuario + "/" + nomeArquivo;
        try {
            Path arquivo = carrega(caminhoDoUsuario);
            Resource resource = new UrlResource(arquivo.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new ExcecaoArquivoNaoEncontrado(
                        "Não foi possível ler o arquivo: " + nomeArquivo);

            }
        }
        catch (MalformedURLException e) {
            throw new ExcecaoArquivoNaoEncontrado("Não foi possível ler o arquivo: " + nomeArquivo, e);
        }
    }

    @Override
    public void exclui(String nomeArquivo, String nomeUsuario) {
        Resource arquivoDeletado = carregaRecurso(nomeArquivo, nomeUsuario);
        try {
            arquivoDeletado.getFile().delete();
        } catch (IOException e) {
            throw new ExcecaoArquivoNaoEncontrado("Não foi possível ler o arquivo: " + nomeArquivo, e);
        }
    }
}
