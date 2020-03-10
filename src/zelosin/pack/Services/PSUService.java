package zelosin.pack.Services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import zelosin.pack.Configurations.Query.QueryConfigurations;
import zelosin.pack.Configurations.Query.QueryTypeAction;
import zelosin.pack.Data.AJAXConfiguration;
import zelosin.pack.Data.Abstract.DepartmentMemberInformation;
import zelosin.pack.Data.DepartmentMember;
import zelosin.pack.Data.ScienceWork.ScienceWork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PSUService {
    private static Document mDepartmentMembersDocument;
    private static Document mGroupsListDocument;
    private static Document mGroupDocument;

    private static final String WORK_LIST_API = "https://lk.pnzgu.ru/ajax/portfolio/";
    private static final String WORK_LIST_API_TYPE = "/type/";

    private static Document mMemberWorksDocument;

    public static Document makeJSOUPQuery(String pURL) {
        Document mReturningDocument = null;
        System.setProperty("javax.net.ssl.trustStore", "/path/to/web2.uconn.edu.jks");
        try {
            mReturningDocument = Jsoup.connect(pURL)
                    .validateTLSCertificates(false)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .ignoreHttpErrors(true)
                    .get();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        return mReturningDocument;
    }

    public static Document makeAJAXQueryForDepartmentMember(String pProfileID, String pAJAXQuery) {
        Document mReturningDocument = null;
        try {
            mReturningDocument = Jsoup.connect(WORK_LIST_API + pProfileID + WORK_LIST_API_TYPE + pAJAXQuery)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .ignoreHttpErrors(true)
                    .get();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        return mReturningDocument;
    }

    private static void getPersonProfileLinkFromGroupList(DepartmentMember pMember){
        mGroupDocument = PSUService.makeJSOUPQuery(
                QueryConfigurations.mGroupListLink + "/" +
                mGroupsListDocument.select(
                "a:contains("+pMember.getmGroupNumber()+")").first().attr("href").replaceAll(".+[/]", ""));
                pMember.setPSUProfileLink( QueryConfigurations.mProfileService +
                mGroupDocument.select("a:contains("+pMember.getmName()+")").first().attr("href")
        );
    }

    public static void assignDepartmentMemberList(QueryTypeAction pQueryType) {
        mDepartmentMembersDocument = PSUService.makeJSOUPQuery(QueryConfigurations.mSessionDepartmentLink);
        for (Element mDepartmentMember : mDepartmentMembersDocument.select(".nps-row-th")){
            DepartmentMember tMember = DepartmentMember.mDepartmentMembersList.get(mDepartmentMember.select("a").last().text());
            if(tMember != null)
                tMember.setPSUProfileLink(mDepartmentMember.select("a").last().attr("href"));
        }
        if(DepartmentMemberInformation.mAssignedProfileCount != DepartmentMember.mDepartmentMembersList.size()) {
            mGroupsListDocument = PSUService.makeJSOUPQuery(QueryConfigurations.mGroupListLink);
            DepartmentMember.mDepartmentMembersList.forEach((key, value) -> {
                if (value.getPSUProfileLink() == null)
                    getPersonProfileLinkFromGroupList(value);
            });
        }
        DepartmentMember.mDepartmentMembersList.forEach((key, value) ->{
            assignScienceWorksToDepartmentMember(
                    pQueryType,
                    value
            );
        });
    }

    public static void assignScienceWorksToDepartmentMember(QueryTypeAction pQueryType, DepartmentMember tDepartmentMember) {
        mMemberWorksDocument = PSUService.makeJSOUPQuery(tDepartmentMember.getPSUProfileLink());

        ArrayList tSectionArray = tDepartmentMember.getSectionArray(QueryTypeAction.Basic, "b_psu_profile_basic_information");
        ScienceWork tBasicInformation = new ScienceWork();
        tSectionArray.add(tBasicInformation);


        for(Element tElement: mMemberWorksDocument.select("[class=\"mr-2\"]").select("a")){
            String tProfileLink = tElement.attr("href");
            if(tProfileLink.contains("elibrary")) tBasicInformation.mScienceWorkInformation.put("b_elibrary_link", tProfileLink);
            else if(tProfileLink.contains("scopus")) tBasicInformation.mScienceWorkInformation.put("b_scopus_link", tProfileLink);
            else if(tProfileLink.contains("publons")) tBasicInformation.mScienceWorkInformation.put("b_publons_link", tProfileLink);
        }

        Elements tWorkElements = mMemberWorksDocument.select(".ru_wrapper_user_row").select(".w-75");
        tBasicInformation.mScienceWorkInformation.put("b_academic_degree", tWorkElements.get(0).text());
        tBasicInformation.mScienceWorkInformation.put("b_academic_rank", tWorkElements.get(1).text());
        tBasicInformation.mScienceWorkInformation.put("b_name", tDepartmentMember.getmName());

        outcrop:
        for(Element tPositionElement : mMemberWorksDocument.selectFirst("#collapse_rows").select(".ru_wrapper_user_row"))
            if(tPositionElement.childNodeSize() > 7) {
                for (Element tWrapElement : tPositionElement.children()) {
                    if (tWrapElement.childNodeSize() == 2) {
                        try {
                            if (tWrapElement.children().first().text().equals("Тип:")) {
                                tBasicInformation.mScienceWorkInformation.put("b_position", tWrapElement.children().get(1).text());
                                break outcrop;
                            }
                        } catch (NullPointerException n) {
                            break outcrop;
                        }
                    }
                }
            }
        switch (pQueryType){
            case Document: {parseMembersSection("#collapseDoc", pQueryType, tDepartmentMember); break;}
            case Resource: {parseMembersSection("#collapseIR", pQueryType, tDepartmentMember);; break;}
            case NIR: {parseMembersSection("#collapseNir", pQueryType, tDepartmentMember);; break;}
            case RID: {parseMembersSection("#collapseRid", pQueryType, tDepartmentMember);; break;}
            case ALL: {
                Map<QueryTypeAction, String> tCSSMap = new HashMap<>(){{
                    put(QueryTypeAction.Resource, "#collapseIR");
                    put(QueryTypeAction.Document, "#collapseDoc");
                    put(QueryTypeAction.NIR, "#collapseNir");
                    put(QueryTypeAction.RID, "#collapseRid");
                }};
                tCSSMap.forEach((key, value) -> {
                    parseMembersSection(value, key, tDepartmentMember);
                });
                break;
            }
        }
    }

    private static void parseMembersSection(String tCSSQuery, QueryTypeAction pQueryType, DepartmentMember tDepartmentMember){
        Elements tWorkElements = mMemberWorksDocument.select(tCSSQuery);
        if(tWorkElements.size() == 0)
            return;
        tWorkElements = tWorkElements.first().select(".btn.btn-link");
        Document tWorkDocument;
        for (Element tWorkType : tWorkElements) {
            var tAJAXParam = AJAXConfiguration.mAJAXConfigurationsList.get(pQueryType).get(tWorkType.text()).toArray();
            if(tAJAXParam.length == 0)
                continue;
            tWorkDocument = PSUService.makeAJAXQueryForDepartmentMember(
                    tDepartmentMember.getmPSUProfileID(),
                    (String)tAJAXParam[0]
            );
            for (Element tWork : tWorkDocument.select(".showed"
                    +pQueryType.ordinal()
                    +"-"
                    + ((String)tAJAXParam[0]).replaceAll("\\D+","").substring(1)
            ))
                tDepartmentMember.getSectionArray(pQueryType, (String)tAJAXParam[1]).add(new ScienceWork(tWork.select("a").first().attr("href")));
        }
    }

    private static void parsePSUScienceWorkPage(ScienceWork tScienceWork, QueryTypeAction pQueryType){
        Document mCurrentDocument = PSUService.makeJSOUPQuery(tScienceWork.getmScienceWorkLink());
        String tKey;
        tScienceWork.mScienceWorkInformation.put(QueryConfigurations.QueryConfiguration.getVariable(pQueryType, "Наименование:"), mCurrentDocument.select("h1").last().text());

        for(Element tTableElement : mCurrentDocument.select("tr")) {
            tKey = tTableElement.select("td").first().text();
            if(tKey.equals("Авторы (ПГУ):")) {
                tScienceWork.mScienceWorkInformation.put(
                        QueryConfigurations.QueryConfiguration.getVariable(pQueryType, tKey),
                        tTableElement.select("td").last().text().replaceAll("\\s\\[\\d+\\]", "")
                );
                continue;
            }
            if (tKey.equals("Место хранения:") || tKey.equals("Категория:"))
                tScienceWork.mScienceWorkInformation.put(
                        QueryConfigurations.QueryConfiguration.getVariable(pQueryType, tKey),
                        tTableElement.select("td").last().previousElementSibling().text()
                );
            else
                tScienceWork.mScienceWorkInformation.put(
                        QueryConfigurations.QueryConfiguration.getVariable(pQueryType, tKey),
                        tTableElement.select("td").last().text()
                );
        }

    }

    public static void parseScienceWorkPageByQueryType(QueryTypeAction pQueryType){
        DepartmentMember.mDepartmentMembersList.forEach((tName, tMember) ->{
            if(pQueryType.equals(QueryTypeAction.ALL)){
                // TODO
            }
            else
                tMember.mMemberInformationList.row(pQueryType).forEach((tSectionName, tScienceWorkArray)-> {
                    tScienceWorkArray.forEach((tWork) -> parsePSUScienceWorkPage(tWork, pQueryType));
                });
        });
    }

    public static void parseScienceWorkPageByQueryTypeAndSectionName(QueryTypeAction pQueryType, String pSectionName){
        DepartmentMember.mDepartmentMembersList.forEach((tName, tMember) ->{
            var tScienceWorkArray = tMember.mMemberInformationList.get(pQueryType, pSectionName);
            if(tScienceWorkArray != null)
                tScienceWorkArray.forEach((tWork) -> parsePSUScienceWorkPage(tWork, pQueryType));
        });
    }
}





