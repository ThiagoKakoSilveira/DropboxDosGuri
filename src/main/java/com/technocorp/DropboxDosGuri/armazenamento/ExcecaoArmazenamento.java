package com.technocorp.DropboxDosGuri.armazenamento;

public class ExcecaoArmazenamento extends RuntimeException {

    public ExcecaoArmazenamento(String mensagem) {
        super(mensagem);
    }

    public ExcecaoArmazenamento(String mensagem, Throwable causa){
        super(mensagem, causa);
    }
}
