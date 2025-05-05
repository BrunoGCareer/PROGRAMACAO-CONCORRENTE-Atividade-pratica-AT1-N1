import java.io.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando validação com 1 thread...");
        
        String pastaArquivos = "cpfs";
        AtomicInteger validos = new AtomicInteger(0);
        AtomicInteger invalidos = new AtomicInteger(0);
        long inicio = System.currentTimeMillis();
        
        File pasta = new File(pastaArquivos);
        File[] arquivos = pasta.listFiles();
        
        if (arquivos == null || arquivos.length == 0) {
            System.out.println("Nenhum arquivo encontrado na pasta: " + pastaArquivos);
            return;
        }
        
        System.out.println("Encontrados " + arquivos.length + " arquivos para processar");
        
        Thread threadUnica = new Thread(() -> {
            for (File arquivo : arquivos) {
                try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
                    String cpf;
                    while ((cpf = leitor.readLine()) != null) {
                        if (CPFValidator.validaCPF(cpf)) {
                            validos.incrementAndGet();
                        } else {
                            invalidos.incrementAndGet();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Erro ao ler arquivo: " + arquivo.getName());
                    e.printStackTrace();
                }
            }
        });
        
        threadUnica.start();
        
        try {
            threadUnica.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        long fim = System.currentTimeMillis();
        long tempoTotal = fim - inicio;
        
        System.out.println("\nRESULTADOS:");
        System.out.println("CPFs válidos: " + validos.get());
        System.out.println("CPFs inválidos: " + invalidos.get());
        System.out.println("Tempo total: " + tempoTotal + " milissegundos");
        
        try (PrintWriter writer = new PrintWriter("versao_1_thread.txt")) {
            writer.println("Tempo de execução: " + tempoTotal + " ms");
            writer.println("CPFs válidos: " + validos.get());
            writer.println("CPFs inválidos: " + invalidos.get());
        } catch (FileNotFoundException e) {
            System.err.println("Erro ao salvar resultados");
            e.printStackTrace();
        }
    }
}