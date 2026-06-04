package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import app.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.uteis.Pontuacao;
import repository.PontuacaoRepository;
import database.Database;

public class PontuacaoController implements Initializable {

    @FXML
    private TableView<Pontuacao> tableViewPontuacao;

    @FXML
    private TableColumn<Pontuacao, String> colJogador1;
    @FXML
    private TableColumn<Pontuacao, String> colJogador2;
    @FXML
    private TableColumn<Pontuacao, String> colVencedor;
    @FXML
    private TableColumn<Pontuacao, Integer> colNumeroJogadas;
    @FXML
    private TableColumn<Pontuacao, Long> colDuracao;

    @FXML
    private TableColumn<Pontuacao, String> colDataPartida;

    private PontuacaoRepository pontuacaoRepository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializa o repositório com a conexão do banco
        pontuacaoRepository = new PontuacaoRepository(Database.getInstance().getConnection());

        // Carrega os dados
        configurarColunas();
        carregarPontuacoes();
    }

    private void carregarPontuacoes() {
        // Busca as pontuações do banco
        java.util.List<Pontuacao> listaPontuacoes = pontuacaoRepository.listarPontuacoes();

        // Converte para ObservableList (necessário para TableView)
        ObservableList<Pontuacao> observableList = FXCollections.observableArrayList(listaPontuacoes);

        // Adiciona os dados à TableView
        tableViewPontuacao.setItems(observableList);
    }

    private void configurarColunas() {
        colJogador1.setCellValueFactory(new PropertyValueFactory<>("jogador1"));
        colJogador2.setCellValueFactory(new PropertyValueFactory<>("jogador2"));
        colVencedor.setCellValueFactory(new PropertyValueFactory<>("vencedor"));
        colNumeroJogadas.setCellValueFactory(new PropertyValueFactory<>("numeroJogadas"));
        colDuracao.setCellValueFactory(new PropertyValueFactory<>("duracao"));


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        colDataPartida.setCellValueFactory(cellData -> {
            LocalDateTime data = cellData.getValue().getDataPartida();
            String dataFormatada = data.format(formatter);
            return new SimpleStringProperty(dataFormatada);
        });
    }

    public void viewMenu() {
        Main.changeScreen("menu.fxml");
    }
}