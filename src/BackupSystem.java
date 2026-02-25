import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

public class BackupSystem {

    private static final DateTimeFormatter ARQUIVO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
    private static final DateTimeFormatter HUMAN_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        // Uso:
        // java BackupSystem "<origem>" "<pastaBase>" [MOVE|COPY]
        if (args.length < 2) {
            System.out.println("Uso:");
            System.out.println("  java BackupSystem <origem> <pastaBase> [MOVE|COPY]");
            System.out.println("Exemplo:");
            System.out.println("  java BackupSystem \"D:\\\\BACKUP NOVO\\\\Downloads\" \"D:\\\\joao-arquivos\" COPY");
            System.out.println("  java BackupSystem \"D:\\\\BACKUP NOVO\\\\Downloads\" \"D:\\\\joao-arquivos\" MOVE");
            return;
        }

        Path origem = Paths.get(args[0]);
        Path pastaBase = Paths.get(args[1]);

        String modo = (args.length >= 3) ? args[2].trim().toUpperCase(Locale.ROOT) : "MOVE";
        boolean copiar = modo.equals("COPY");
        if (!copiar && !modo.equals("MOVE")) {
            System.out.println("Modo inválido. Use MOVE ou COPY.");
            return;
        }

        BackupResult result = organizarArquivos(origem, pastaBase, copiar);

        try {
            gerarRelatorio(pastaBase, origem, copiar, result);
            System.out.println("Relatório gerado com sucesso.");
        } catch (IOException e) {
            System.out.println("Falha ao gerar relatório: " + e.getMessage());
        }

        System.out.println("\n===== RESUMO =====");
        System.out.println("Origem : " + origem);
        System.out.println("Destino: " + pastaBase);
        System.out.println("Modo   : " + (copiar ? "COPY" : "MOVE"));
        System.out.println("Processados     : " + result.processados);
        System.out.println("Ignorados       : " + result.ignorados);
        System.out.println("Erros           : " + result.erros);
    }

    public static BackupResult organizarArquivos(Path origem, Path pastaBase, boolean copiar) {
        BackupResult result = new BackupResult();

        if (!Files.exists(origem) || !Files.isDirectory(origem)) {
            result.erros++;
            result.errosDetalhados.add("Origem inválida: " + origem);
            System.out.println("Origem não encontrada ou não é pasta: " + origem);
            return result;
        }

        try {
            Files.createDirectories(pastaBase);
        } catch (IOException e) {
            result.erros++;
            result.errosDetalhados.add("Não criou pasta base: " + pastaBase + " | " + e.getMessage());
            System.out.println("Não foi possível criar pasta base: " + pastaBase);
            return result;
        }

        try (Stream<Path> stream = Files.list(origem)) {
            stream.forEach(arquivo -> processarUmArquivo(arquivo, pastaBase, copiar, result));
        } catch (IOException e) {
            result.erros++;
            result.errosDetalhados.add("Erro listando origem: " + origem + " | " + e.getMessage());
            System.out.println("Erro ao listar a pasta: " + origem);
        }

        return result;
    }

    private static void processarUmArquivo(Path arquivo, Path pastaBase, boolean copiar, BackupResult result) {
        try {
            if (!Files.isRegularFile(arquivo)) {
                result.ignorados++;
                return;
            }

            String nome = arquivo.getFileName().toString();
            String categoria = escolherCategoria(nome);

            Path pastaCategoria = pastaBase.resolve(categoria);
            Files.createDirectories(pastaCategoria);

            Path destinoFinal = pastaCategoria.resolve(nome);

            if (copiar) {
                Files.copy(arquivo, destinoFinal, StandardCopyOption.REPLACE_EXISTING);
                result.processados++;
                result.itensProcessados.add("[COPY] " + nome + " -> " + categoria);
            } else {
                Files.move(arquivo, destinoFinal, StandardCopyOption.REPLACE_EXISTING);
                result.processados++;
                result.itensProcessados.add("[MOVE] " + nome + " -> " + categoria);
            }

        } catch (IOException e) {
            result.erros++;
            result.errosDetalhados.add("Falha com " + arquivo.getFileName() + " | " + e.getMessage());
        }
    }

    /**
     * 1) EXOCAD: só entra se o nome tiver palavras de implante/anatomia/biblioteca/etc
     * 2) STL: extensão .stl
     * 3) PACOTES: .rar .zip .7z
     * 4) OUTROS: resto
     */
    private static String escolherCategoria(String nomeArquivo) {
        String lower = nomeArquivo.toLowerCase(Locale.ROOT);

        // EXOCAD por palavras-chave (implantes, anatomia, bibliotecas etc.)
        if (contemQualquer(lower,
                "exocad",
                "implant", "implante",
                "anatomia", "anatomy",
                "library", "biblioteca",
                "scanbody",
                "abutment",
                "component", "componente",
                "dentalcad",
                "tooth", "dente",
                "coroa", "crown")) {
            return "EXOCAD";
        }

        // por extensão
        String ext = extensao(lower);

        if (ext.equals("stl")) return "STL";

        if (ext.equals("rar") || ext.equals("zip") || ext.equals("7z")) return "PACOTES";

        return "OUTROS";
    }

    private static String extensao(String nome) {
        int idx = nome.lastIndexOf('.');
        if (idx < 0 || idx == nome.length() - 1) return "";
        return nome.substring(idx + 1);
    }

    private static boolean contemQualquer(String texto, String... palavras) {
        for (String p : palavras) {
            if (texto.contains(p)) return true;
        }
        return false;
    }

    private static void gerarRelatorio(Path pastaBase, Path origem, boolean copiar, BackupResult result) throws IOException {
        LocalDateTime agora = LocalDateTime.now();
        String nomeRelatorio = "relatorio-organizacao-" + ARQUIVO_DATA.format(agora) + ".txt";
        Path relatorioPath = pastaBase.resolve(nomeRelatorio);

        List<String> linhas = new ArrayList<>();
        linhas.add("===== RELATÓRIO DE ORGANIZAÇÃO =====");
        linhas.add("Data/Hora: " + HUMAN_DATA.format(agora));
        linhas.add("Origem   : " + origem);
        linhas.add("Destino  : " + pastaBase);
        linhas.add("Modo     : " + (copiar ? "COPY" : "MOVE"));
        linhas.add("");
        linhas.add("Processados: " + result.processados);
        linhas.add("Ignorados : " + result.ignorados);
        linhas.add("Erros     : " + result.erros);
        linhas.add("");

        if (!result.itensProcessados.isEmpty()) {
            linhas.add("===== ITENS =====");
            linhas.addAll(result.itensProcessados);
            linhas.add("");
        }

        if (!result.errosDetalhados.isEmpty()) {
            linhas.add("===== ERROS (DETALHES) =====");
            linhas.addAll(result.errosDetalhados);
            linhas.add("");
        }

        Files.write(relatorioPath, linhas, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("Relatório salvo em: " + relatorioPath);
    }

    static class BackupResult {
        int processados = 0;
        int ignorados = 0;
        int erros = 0;

        List<String> itensProcessados = new ArrayList<>();
        List<String> errosDetalhados = new ArrayList<>();
    }
}