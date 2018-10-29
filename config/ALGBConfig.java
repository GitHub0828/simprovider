
package com.android.settings.simprovider.algb;

import com.android.settings.simprovider.SimProviderPolicy;

import com.android.settings.R;

public class ALGBConfig {
    public static void init() {
        new SimProviderPolicy("23410") {{
            add("O2 Pay Monthly", "O2 Pay Monthly", "mobile.o2.co.uk");
            add("O2 Pay As You Go", "O2 Pay As You Go", "payandgo.o2.co.uk");
            add("TESCO", "TESCO", "prepay.tesco-mobile.com");
            add("giffgaff", "giffgaff", "giffgaff.com");
            add("TalkTalk", "TalkTalk", "mobile.talktalk.co.uk");
            add("Jump", "Jump", "mobiledata").onlyInternet();
        }};

        new SimProviderPolicy("23415") {{
            add("Vodafone Contract", "Contract WAP", "wap.vodafone.co.uk");
            add("Vodafone PAYG", "PAYG WAP", "pp.vodafone.co.uk");
            add("Talkmobile Contract", "Talkmobile Internet", "talkmobile.co.uk")
                    .addMms("Talkmobile MMS", "talkmobile.co.uk");
            add("Talkmobile PAYG", "Talkmobile PAYG Internet", "payg.talkmobile.co.uk")
                    .addMms("Talkmobile PAYG MMS", "payg.talkmobile.co.uk");
            add("Sainsburys Contract", "Sainsbury's Contract", "mobilebysainsburys.co.uk")
                    .addMms("Sainsbury's Contract MMS", "mobilebysainsburys.co.uk");
            add("Sainsburys PAYG", "Sainsbury's PAYG", "payg.mobilebysainsburys.co.uk")
                    .addMms("Sainsbury's PAYG MMS", "payg.mobilebysainsburys.co.uk");
            add("Lebara", "Lebara", "uk.lebara.mobi");
            add("TalkTalk", "TalkTalk", "mobile.talktalk.co.uk");
        }};

        new SimProviderPolicy("23420") {{
            add("3", "3", "three.co.uk");
            add("iD", "iD", "id");
        }};

       /*new SimProviderPolicy("23430") {{
            add("EE", "Internet", "everywhere")
                    .addMms("MMS", "eezone");
            add("Virgin Mobile", "Virgin Media Mobile Internet", "goto.virginmobile.uk")
                    .addMms("Virgin MMS", "goto.virginmobile.uk");
            add("BT", "Internet", "everywhere")
                    .addMms("MMS", "eezone");
            add("BT OnePhone", "BT OnePhone Internet", "internet.btonephone.com")
                    .addMms("BT OnePhone MMS", "mms.btonephone.com");
            add("ASDA Mobile", "Asda Internet", "everywhere")
                    .addMms("Asda MMS", "eezone");
        }}; */
    }
}