/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Requests.Catalog;

import java.util.*;
import java.util.concurrent.FutureTask;
import neutrino.Environment;
import neutrino.Net.ServerHandler;
import neutrino.PacketsInformation.ServerEvents;
import neutrino.Requests.Handler;
import neutrino.System.ServerMessage;
import neutrino.CatalogManager.CatalogPage;
import neutrino.CatalogManager.CatalogItem;
import neutrino.ItemManager.ItemInformation;

/**
 *
 * @author Julián
 */
public class LoadCatalogPageForm extends Handler implements Runnable {

    private ServerHandler Client;
    private Environment Server;
    private FutureTask T;
    @Override
    public void Load(ServerHandler Client, Environment Server, FutureTask T) throws Exception
    {
        this.Client = Client;
        this.Server = Server;
        this.T = T;
    }
    
    public void run() {
        try {
            Integer I = Client.in.readInt();
            //Server.WriteLine("To Load Page: " + I);
            CatalogPage PageData = CatalogPage.Pages.get(I);
            if (!PageData.PageEnabled) {
                return;
            }

            ServerMessage Catalog = new ServerMessage(ServerEvents.SendCatalogPageData);
            Catalog.writeInt(PageData.Id);
            if ("frontpage".equals(PageData.Type)) {
                Catalog.writeUTF("frontpage3");
                Catalog.writeInt(3);
                Catalog.writeUTF("catalog_frontpage_headline_shop_DKCOMNLDE_02");
                Catalog.writeUTF("topstory_xmas11_03");
                Catalog.writeUTF("");
                Catalog.writeInt(11);
                Catalog.writeUTF("OMG ITACHI RULES!");
                Catalog.writeUTF("Hey Habbo, you can see that :O!");
                Catalog.writeUTF("Wow! Nacha marica");
                Catalog.writeUTF("Â¿CÃ³mo conseguir Habbo CrÃ©ditos?");
                Catalog.writeUTF("You can get Habbo Credits via Prepaid Cards, Home Phone, Credit Card, Mobile, completing offers and more!\n" + (char) 10 + "" + (char) 10 + " To redeem your Habbo Credits, enter your voucher code below.");
                Catalog.writeUTF("Voucheras aquÃ­ nena:");
                Catalog.writeUTF("Rares");
                Catalog.writeUTF("#FEFEFE");
                Catalog.writeUTF("#FEFEFE");
                Catalog.writeUTF("Bla bla bla!");
                Catalog.writeUTF("magic.credits");
            } else if ("guild_frontpage".equals(PageData.Type)) {
                Catalog.writeUTF("guild_frontpage");
                Catalog.writeInt(2);
                Catalog.writeUTF(PageData.HeadLine);
                Catalog.writeUTF(PageData.Teaser);
                Catalog.writeInt(3);
                Catalog.writeUTF("lololol");
                Catalog.writeUTF("* YES!\n* blahblahblah\n* :O!\n* SOY LOCA CON MI TIGRE (8)");
                Catalog.writeUTF("ITACHI RULES?");
            } else if("spacepage".equals(PageData.Type))
            {
                Catalog.writeUTF("spaces_new");
                Catalog.writeInt(1);
                Catalog.writeUTF(PageData.HeadLine);
                Catalog.writeInt(1);
                Catalog.writeUTF(PageData.Text);
            } else if("musicshop".equals(PageData.Type))
            {
                Catalog.writeUTF("soundmachine");
                Catalog.writeInt(2);
                Catalog.writeUTF(PageData.HeadLine);
                Catalog.writeUTF(PageData.Teaser);
                Catalog.writeInt(2);
                Catalog.writeUTF(PageData.Text);
                Catalog.writeUTF(PageData.TextDetails);
            } else if("club_buy".equals(PageData.Type))
            {
                Catalog.writeUTF("vip_buy");
                Catalog.writeInt(2);
                Catalog.writeUTF("ctlg_buy_vip_header");
                Catalog.writeUTF("ctlg_buy_vip_picture");
                Catalog.writeInt(0);                
            } else {
                Catalog.writeUTF("default_3x3");
                Catalog.writeInt(3);
                Catalog.writeUTF(PageData.HeadLine);
                Catalog.writeUTF(PageData.Teaser);
                Catalog.writeUTF(PageData.TextSpecial);
                Catalog.writeInt(3);
                Catalog.writeUTF(PageData.Text);
                Catalog.writeUTF(PageData.TextDetails);
                Catalog.writeUTF(PageData.TextTeaser);
            }
            Map<Integer, CatalogItem> Items = null;
            if("news".equals(PageData.Type))
                Items = CatalogItem.GetLast25Ids();
            else
                Items = CatalogItem.GetItemsForPageId(PageData.Id);
            if (PageData.Id != 2) {
                Catalog.writeInt(Items.size()); // Count Items
                Iterator reader = Items.entrySet().iterator();
                while (reader.hasNext()) {
                    CatalogItem ItemData = (CatalogItem) (((Map.Entry) reader.next()).getValue());
                    Catalog.writeInt(ItemData.Id); // ItemId
                    Catalog.writeUTF(ItemData.Name); // Name
                    Catalog.writeInt(ItemData.CostCredits); // Cost Credits
                    Catalog.writeInt(ItemData.CostPixels); // Activity points
                    Catalog.writeInt(0); // quest shit
                    Catalog.writeBoolean(false); // Can gift?
                    Catalog.writeInt(ItemData.ItemIds.size()); // count of items (for deals)
                    Iterator treader = ItemData.ItemIds.iterator();
                    while (treader.hasNext()) {
                        int ItemId = (Integer)treader.next();
                        ItemInformation furniData = ItemInformation.Items.get(ItemId);
                        Catalog.writeUTF(furniData.Type); // type: s, i, h, etc
                        Catalog.writeInt(furniData.SpriteId); // spriteid
                        if (ItemData.Name.contains("wallpaper"))
                            Catalog.writeUTF(ItemData.Name.split("_")[2]); // shit for wallpapers
                        else if(ItemData.Name.contains("floor"))
                            Catalog.writeUTF(ItemData.Name.split("_")[2]); // shit for wallpapers
                        else if(ItemData.Name.contains("landscape"))
                            Catalog.writeUTF(ItemData.Name.split("_")[2]); // shit for wallpapers
                        else
                            Catalog.writeUTF(ItemData.ExtraInformation); // shit for music and other shit
                        int Amount = ItemData.Amount;
                        if(ItemData.ExtraAmounts.containsKey(ItemId))
                        Amount = ItemData.ExtraAmounts.get(ItemId);
                        Catalog.writeInt(Amount); // amount of items
                        Catalog.writeInt(-1); // separe
                    }
                    Catalog.writeInt(ItemData.IsClub); // is club/vip/etc shit
                }
            } else {
                Catalog.writeInt(0);
            }
            Catalog.writeInt(-1); // Final Shit
            Catalog.Send(Client.Socket);
            
            // Extra packets
            if("club_buy".equals(PageData.Type))
            {
                ServerMessage Club = new ServerMessage(ServerEvents.ClubBuy);
                Map<Integer, CatalogItem> cItems = CatalogItem.GetItemsForPageId(PageData.Id);
                Club.writeInt(cItems.size()); // Count Items
                Iterator reader = cItems.entrySet().iterator();
                while (reader.hasNext()) {
                    CatalogItem ItemData = (CatalogItem) (((Map.Entry) reader.next()).getValue());
                    Club.writeInt(ItemData.Id); // Id
                    Club.writeUTF(ItemData.Name); // Name
                    Club.writeInt(ItemData.CostCredits); // Creidts
                    Club.writeBoolean(true); // ? VIP Maybe?
                    int MonthsorDays = Integer.parseInt(ItemData.Name.split("_")[3]);
                    if(ItemData.Name.split("_")[4].contains("MONTH"))
                    {
                        Club.writeInt(MonthsorDays); // Months Left
                        Club.writeInt(3 * MonthsorDays); // Days Left
                        Club.writeInt(3 * MonthsorDays); // ??
                    } else {
                        Club.writeInt(0); // Months Left
                        Club.writeInt(MonthsorDays); // Days left
                        Club.writeInt(MonthsorDays); // ??
                    }
                    Club.writeInt(2012); //  // futureyear
                    Club.writeInt(3); // future month
                    Club.writeInt(27); // future day                    
                }
                Club.writeInt(1);
                Club.Send(Client.Socket);
            }
        } catch (Exception e) {
            Server.WriteLine(e);
        }
    }
}
