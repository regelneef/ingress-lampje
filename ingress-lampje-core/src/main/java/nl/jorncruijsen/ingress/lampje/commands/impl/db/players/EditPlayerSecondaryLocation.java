package nl.jorncruijsen.ingress.lampje.commands.impl.db.players;

import java.sql.SQLException;

import nl.jorncruijsen.ingress.lampje.DBRepository;
import nl.jorncruijsen.ingress.lampje.domain.game.Player;
import nl.jorncruijsen.messaging.domain.Message;
import nl.jorncruijsen.messaging.providers.AbstractMessageChannel;

public class EditPlayerSecondaryLocation extends EditPlayerInfoBaseCommand {

  public EditPlayerSecondaryLocation() {
  }

  @Override
  boolean validate(final AbstractMessageChannel chat, final String data) {
    return true;
  }

  @Override
  String getOldData(final Player player) {
    return player.getLocationSecondary();
  }

  @Override
  public void trigger(final AbstractMessageChannel chat, final Message message) {
    final String body = message.getText();
    final String[] splittedBody = body.split(" ", 3);
    String text = null;

    if (splittedBody.length == 3) {
      final String nickname = splittedBody[1];
      final String data = splittedBody[2];
      //
      // if (validate(chat, data)) {
      Player originalPlayer = null;
      try {
        originalPlayer = DBRepository.getPlayer(nickname);
        if (originalPlayer != null) {
          final String secondaryLocations = originalPlayer.getLocationSecondary();
          originalPlayer.setLocationSecondary(originalPlayer.getLocationSecondary() == null ? data : (originalPlayer.getLocationSecondary() + ", " + data));
          DBRepository.updatePlayer(originalPlayer);
          final Player updatedPlayer = DBRepository.getPlayer(nickname);
          // final IngressPlayer player =
          // L8PlayersSpreadsheetUtil.editAgent(nickname, columnName,
          // data, edit);
          //
          // final Player player =
          // if (player == null) {
          // text = "Agent " + nickname + " not found.";
          // } else {
          // final String oldData = getOldData(player);
          // final String newData = edit ? data : (oldData != null ?
          // oldData + " "
          // : "") + data;
          text = "Updated agent " + nickname + ". Old: '" + secondaryLocations + "' - new: '" + updatedPlayer.getLocationSecondary() + "'.";
          // }

          // } catch (ServiceException | IOException e) {
          // text =
          // "Server made a booboo while trying to edit the agent information. Try again later. Contact admin if the problem persists.";
          // e.printStackTrace();
        } else {
          text = "player not found";
        }
      } catch (final SQLException e) {
        // TODO Auto-generated catch block
        text = "Server made a database booboo while trying to edit the agent information. Try again later. Contact admin if the problem persists.";

        e.printStackTrace();
      }
    } else {
      // text = "No no no no. I need !" + (edit ? "edit" : "add") +
      // "[something] [agentname] [updateInfo]";
      text = "RTFM";
    }

    if (text != null) {
      chat.sendMessage(text);
    }

  }

}
