package com.example.idielectronica2.rescueanimals.FTP;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import android.os.Environment;
import android.util.Log;

public class ClassFTP {
    FTPClient mFtp;
    String TAG = "classeFTP";

    public FTPFile[] Dir(String Diretorio) {
        try {
            FTPFile[] ftpFiles = mFtp.listFiles(Diretorio);
            return ftpFiles;
        }
        catch(Exception e) {
            Log.e(TAG, "Erro: não foi possível  listar os   arquivos e pastas do diretório " + Diretorio + ". " + e.getMessage());
        }

        return null;
    }

    public boolean MudarDiretorio(String Diretorio) {
        try {
            mFtp.changeWorkingDirectory(Diretorio);
        }
        catch(Exception e) {
            Log.e(TAG, "Erro: não foi possível mudar o diretório para " + Diretorio);
        }
        return false;
    }

    public boolean Desconectar() {
        try {
            mFtp.disconnect();
            mFtp = null;
            return true;
        }
        catch (Exception e) {
            Log.e(TAG, "Erro: ao desconectar. " + e.getMessage());
        }

        return false;
    }

    public boolean Conectar(String Host, String Usuario, String Senha, int Porta) {
        try {
            mFtp = new FTPClient();

            mFtp.connect(Host, Porta);

            if (FTPReply.isPositiveCompletion(mFtp.getReplyCode())) {
                boolean status = mFtp.login(Usuario, Senha);

                mFtp.setFileType(FTP.BINARY_FILE_TYPE);
                mFtp.enterLocalPassiveMode();

                return status;
            }
        }
        catch(Exception e) {
            Log.e(TAG, "Erro: não foi possível conectar" + Host);
        }
        return false;
    }

    public boolean Download(String DiretorioOrigem, String ArqOrigem, String ArqDestino) {
        boolean status = false;

        try {
            MudarDiretorio(DiretorioOrigem);

            FileOutputStream desFileStream = new FileOutputStream(ArqDestino);;

            mFtp.setFileType(FTP.BINARY_FILE_TYPE);
            mFtp.enterLocalActiveMode();
            mFtp.enterLocalPassiveMode();

            status =  mFtp.retrieveFile(ArqOrigem, desFileStream);
            desFileStream.close();
            Desconectar();

            return status;
        }
        catch (Exception e) {
            Log.e(TAG, "Erro: Falha ao efetuar download. " + e.getMessage());
        }

        return status;
    }

    public boolean Upload(String diretorio, String nomeArquivo) {
        boolean status = false;
        try {
            FileInputStream arqEnviar = new  FileInputStream(Environment.getExternalStorageDirectory() + diretorio);
            mFtp.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE);
            mFtp.setFileType(FTPClient.STREAM_TRANSFER_MODE);
            mFtp.storeFile(nomeArquivo, arqEnviar);
            Desconectar();
            return status;
        }
        catch (Exception e) {
            Log.e(TAG, "Erro: Falha ao efetuar Upload. " +  e.getMessage());
        }
        return status;
    }



}
