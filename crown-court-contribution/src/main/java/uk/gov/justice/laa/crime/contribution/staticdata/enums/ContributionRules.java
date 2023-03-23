package uk.gov.justice.laa.crime.contribution.staticdata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum ContributionRules {

    INDICTABLE_11010055(11010055, Outcome.INDICTABLE, null, null, null, null),
    INDICTABLE_11010056(11010056, Outcome.INDICTABLE, Outcome.COMMITTED_FOR_TRIAL, null, null, null),
    INDICTABLE_11010057(11010057, Outcome.INDICTABLE, Outcome.COMMITTED_FOR_TRIAL, Outcome.AQUITTED, null, null),
    INDICTABLE_11010058(11010058, Outcome.INDICTABLE, Outcome.COMMITTED_FOR_TRIAL, Outcome.PART_CONVICTED, null, null),
    INDICTABLE_11010059(11010059, Outcome.INDICTABLE, Outcome.COMMITTED_FOR_TRIAL, Outcome.CONVICTED, null, null),
    INDICTABLE_11010060(11010060, Outcome.INDICTABLE, Outcome.COMMITTED_FOR_TRIAL, Outcome.DISMISSED, null, null),
    INDICTABLE_11010061(11010061, Outcome.INDICTABLE, Outcome.COMMITTED_FOR_TRIAL, Outcome.PART_SUCCESS, null, null),
    INDICTABLE_11010062(11010062, Outcome.INDICTABLE, Outcome.COMMITTED_FOR_TRIAL, Outcome.UNSUCCESSFUL, null, null),
    INDICTABLE_11010063(11010063, Outcome.INDICTABLE, Outcome.COMMITTED_FOR_TRIAL, Outcome.SUCCESSFUL, null, null),
    INDICTABLE_11010064(11010064, Outcome.INDICTABLE, Outcome.COMMITTED_FOR_TRIAL, Outcome.ABANDONED, null, null),
    INDICTABLE_11010065(11010065, Outcome.INDICTABLE, Outcome.SENT_FOR_TRIAL, null, null, null),
    INDICTABLE_11010066(11010066, Outcome.INDICTABLE, Outcome.SENT_FOR_TRIAL, Outcome.AQUITTED, null, null),
    INDICTABLE_11010067(11010067, Outcome.INDICTABLE, Outcome.SENT_FOR_TRIAL, Outcome.PART_CONVICTED, null, null),
    INDICTABLE_11010068(11010068, Outcome.INDICTABLE, Outcome.SENT_FOR_TRIAL, Outcome.CONVICTED, null, null),
    INDICTABLE_11010069(11010069, Outcome.INDICTABLE, Outcome.SENT_FOR_TRIAL, Outcome.DISMISSED, null, null),
    INDICTABLE_11010070(11010070, Outcome.INDICTABLE, Outcome.SENT_FOR_TRIAL, Outcome.PART_SUCCESS, null, null),
    INDICTABLE_11010071(11010071, Outcome.INDICTABLE, Outcome.SENT_FOR_TRIAL, Outcome.UNSUCCESSFUL, null, null),
    INDICTABLE_11010072(11010072, Outcome.INDICTABLE, Outcome.SENT_FOR_TRIAL, Outcome.SUCCESSFUL, null, null),
    INDICTABLE_11010073(11010073, Outcome.INDICTABLE, Outcome.SENT_FOR_TRIAL, Outcome.ABANDONED, null, null),
    INDICTABLE_11010074(11010074, Outcome.INDICTABLE, Outcome.RESOLVED_IN_MAGS, null, null, null),
    INDICTABLE_11010075(11010075, Outcome.INDICTABLE, Outcome.RESOLVED_IN_MAGS, Outcome.AQUITTED, null, null),
    INDICTABLE_11010076(11010076, Outcome.INDICTABLE, Outcome.RESOLVED_IN_MAGS, Outcome.PART_CONVICTED, null, null),
    INDICTABLE_11010077(11010077, Outcome.INDICTABLE, Outcome.RESOLVED_IN_MAGS, Outcome.CONVICTED, null, null),
    INDICTABLE_11010078(11010078, Outcome.INDICTABLE, Outcome.RESOLVED_IN_MAGS, Outcome.DISMISSED, null, null),
    INDICTABLE_11010079(11010079, Outcome.INDICTABLE, Outcome.RESOLVED_IN_MAGS, Outcome.PART_SUCCESS, null, null),
    INDICTABLE_11010080(11010080, Outcome.INDICTABLE, Outcome.RESOLVED_IN_MAGS, Outcome.UNSUCCESSFUL, null, null),
    INDICTABLE_11010081(11010081, Outcome.INDICTABLE, Outcome.RESOLVED_IN_MAGS, Outcome.SUCCESSFUL, null, null),
    INDICTABLE_11010082(11010082, Outcome.INDICTABLE, Outcome.RESOLVED_IN_MAGS, Outcome.ABANDONED, null, null),
    INDICTABLE_11010083(11010083, Outcome.INDICTABLE, Outcome.COMMITTED, null, null, null),
    INDICTABLE_11010084(11010084, Outcome.INDICTABLE, Outcome.COMMITTED, Outcome.AQUITTED, null, null),
    INDICTABLE_11010085(11010085, Outcome.INDICTABLE, Outcome.COMMITTED, Outcome.PART_CONVICTED, null, null),
    INDICTABLE_11010086(11010086, Outcome.INDICTABLE, Outcome.COMMITTED, Outcome.CONVICTED, null, null),
    INDICTABLE_11010087(11010087, Outcome.INDICTABLE, Outcome.COMMITTED, Outcome.DISMISSED, null, null),
    INDICTABLE_11010088(11010088, Outcome.INDICTABLE, Outcome.COMMITTED, Outcome.PART_SUCCESS, null, null),
    INDICTABLE_11010089(11010089, Outcome.INDICTABLE, Outcome.COMMITTED, Outcome.UNSUCCESSFUL, null, null),
    INDICTABLE_11010090(11010090, Outcome.INDICTABLE, Outcome.COMMITTED, Outcome.SUCCESSFUL, null, null),
    INDICTABLE_11010091(11010091, Outcome.INDICTABLE, Outcome.COMMITTED, Outcome.ABANDONED, null, null),
    INDICTABLE_11010092(11010092, Outcome.INDICTABLE, Outcome.APPEAL_TO_CC, null, null, null),
    INDICTABLE_11010093(11010093, Outcome.INDICTABLE, Outcome.APPEAL_TO_CC, Outcome.AQUITTED, null, null),
    INDICTABLE_11010094(11010094, Outcome.INDICTABLE, Outcome.APPEAL_TO_CC, Outcome.PART_CONVICTED, null, null),
    INDICTABLE_11010095(11010095, Outcome.INDICTABLE, Outcome.APPEAL_TO_CC, Outcome.CONVICTED, null, null),
    INDICTABLE_11010096(11010096, Outcome.INDICTABLE, Outcome.APPEAL_TO_CC, Outcome.DISMISSED, null, null),
    INDICTABLE_11010097(11010097, Outcome.INDICTABLE, Outcome.APPEAL_TO_CC, Outcome.PART_SUCCESS, null, null),
    INDICTABLE_11010098(11010098, Outcome.INDICTABLE, Outcome.APPEAL_TO_CC, Outcome.UNSUCCESSFUL, null, null),
    INDICTABLE_11010099(11010099, Outcome.INDICTABLE, Outcome.APPEAL_TO_CC, Outcome.SUCCESSFUL, null, null),
    INDICTABLE_11010100(11010100, Outcome.INDICTABLE, Outcome.APPEAL_TO_CC, Outcome.ABANDONED, null, null),
    SUMMARY_ONLY_11010101(11010101, Outcome.SUMMARY_ONLY, null, null, null, null),
    SUMMARY_ONLY_11010102(11010102, Outcome.SUMMARY_ONLY, Outcome.COMMITTED_FOR_TRIAL, null, null, null),
    SUMMARY_ONLY_11010103(11010103, Outcome.SUMMARY_ONLY, Outcome.COMMITTED_FOR_TRIAL, Outcome.AQUITTED, null, null),
    SUMMARY_ONLY_11010104(11010104, Outcome.SUMMARY_ONLY, Outcome.COMMITTED_FOR_TRIAL, Outcome.PART_CONVICTED, null, null),
    SUMMARY_ONLY_11010105(11010105, Outcome.SUMMARY_ONLY, Outcome.COMMITTED_FOR_TRIAL, Outcome.CONVICTED, null, null),
    SUMMARY_ONLY_11010106(11010106, Outcome.SUMMARY_ONLY, Outcome.COMMITTED_FOR_TRIAL, Outcome.DISMISSED, null, null),
    SUMMARY_ONLY_11010107(11010107, Outcome.SUMMARY_ONLY, Outcome.COMMITTED_FOR_TRIAL, Outcome.PART_SUCCESS, null, null),
    SUMMARY_ONLY_11010108(11010108, Outcome.SUMMARY_ONLY, Outcome.COMMITTED_FOR_TRIAL, Outcome.UNSUCCESSFUL, null, null),
    SUMMARY_ONLY_11010109(11010109, Outcome.SUMMARY_ONLY, Outcome.COMMITTED_FOR_TRIAL, Outcome.SUCCESSFUL, null, null),
    SUMMARY_ONLY_11010110(11010110, Outcome.SUMMARY_ONLY, Outcome.COMMITTED_FOR_TRIAL, Outcome.ABANDONED, null, null),
    SUMMARY_ONLY_11010111(11010111, Outcome.SUMMARY_ONLY, Outcome.SENT_FOR_TRIAL, null, null, null),
    SUMMARY_ONLY_11010112(11010112, Outcome.SUMMARY_ONLY, Outcome.SENT_FOR_TRIAL, Outcome.AQUITTED, null, null),
    SUMMARY_ONLY_11010113(11010113, Outcome.SUMMARY_ONLY, Outcome.SENT_FOR_TRIAL, Outcome.PART_CONVICTED, null, null),
    SUMMARY_ONLY_11010114(11010114, Outcome.SUMMARY_ONLY, Outcome.SENT_FOR_TRIAL, Outcome.CONVICTED, null, null),
    SUMMARY_ONLY_11010115(11010115, Outcome.SUMMARY_ONLY, Outcome.SENT_FOR_TRIAL, Outcome.DISMISSED, null, null),
    SUMMARY_ONLY_11010116(11010116, Outcome.SUMMARY_ONLY, Outcome.SENT_FOR_TRIAL, Outcome.PART_SUCCESS, null, null),
    SUMMARY_ONLY_11010117(11010117, Outcome.SUMMARY_ONLY, Outcome.SENT_FOR_TRIAL, Outcome.UNSUCCESSFUL, null, null),
    SUMMARY_ONLY_11010118(11010118, Outcome.SUMMARY_ONLY, Outcome.SENT_FOR_TRIAL, Outcome.SUCCESSFUL, null, null),
    SUMMARY_ONLY_11010119(11010119, Outcome.SUMMARY_ONLY, Outcome.SENT_FOR_TRIAL, Outcome.ABANDONED, null, null),
    SUMMARY_ONLY_11010120(11010120, Outcome.SUMMARY_ONLY, Outcome.RESOLVED_IN_MAGS, null, null, null),
    SUMMARY_ONLY_11010121(11010121, Outcome.SUMMARY_ONLY, Outcome.RESOLVED_IN_MAGS, Outcome.AQUITTED, null, null),
    SUMMARY_ONLY_11010122(11010122, Outcome.SUMMARY_ONLY, Outcome.RESOLVED_IN_MAGS, Outcome.PART_CONVICTED, null, null),
    SUMMARY_ONLY_11010123(11010123, Outcome.SUMMARY_ONLY, Outcome.RESOLVED_IN_MAGS, Outcome.CONVICTED, null, null),
    SUMMARY_ONLY_11010124(11010124, Outcome.SUMMARY_ONLY, Outcome.RESOLVED_IN_MAGS, Outcome.DISMISSED, null, null),
    SUMMARY_ONLY_11010125(11010125, Outcome.SUMMARY_ONLY, Outcome.RESOLVED_IN_MAGS, Outcome.PART_SUCCESS, null, null),
    SUMMARY_ONLY_11010126(11010126, Outcome.SUMMARY_ONLY, Outcome.RESOLVED_IN_MAGS, Outcome.UNSUCCESSFUL, null, null),
    SUMMARY_ONLY_11010127(11010127, Outcome.SUMMARY_ONLY, Outcome.RESOLVED_IN_MAGS, Outcome.SUCCESSFUL, null, null),
    SUMMARY_ONLY_11010128(11010128, Outcome.SUMMARY_ONLY, Outcome.RESOLVED_IN_MAGS, Outcome.ABANDONED, null, null),
    SUMMARY_ONLY_11010129(11010129, Outcome.SUMMARY_ONLY, Outcome.COMMITTED, null, null, null),
    SUMMARY_ONLY_11010130(11010130, Outcome.SUMMARY_ONLY, Outcome.COMMITTED, Outcome.AQUITTED, null, null),
    SUMMARY_ONLY_11010131(11010131, Outcome.SUMMARY_ONLY, Outcome.COMMITTED, Outcome.PART_CONVICTED, null, null),
    SUMMARY_ONLY_11010132(11010132, Outcome.SUMMARY_ONLY, Outcome.COMMITTED, Outcome.CONVICTED, null, null),
    SUMMARY_ONLY_11010133(11010133, Outcome.SUMMARY_ONLY, Outcome.COMMITTED, Outcome.DISMISSED, null, null),
    SUMMARY_ONLY_11010134(11010134, Outcome.SUMMARY_ONLY, Outcome.COMMITTED, Outcome.PART_SUCCESS, null, null),
    SUMMARY_ONLY_11010135(11010135, Outcome.SUMMARY_ONLY, Outcome.COMMITTED, Outcome.UNSUCCESSFUL, null, null),
    SUMMARY_ONLY_11010136(11010136, Outcome.SUMMARY_ONLY, Outcome.COMMITTED, Outcome.SUCCESSFUL, null, null),
    SUMMARY_ONLY_11010137(11010137, Outcome.SUMMARY_ONLY, Outcome.COMMITTED, Outcome.ABANDONED, null, null),
    SUMMARY_ONLY_11010138(11010138, Outcome.SUMMARY_ONLY, Outcome.APPEAL_TO_CC, null, null, null),
    SUMMARY_ONLY_11010139(11010139, Outcome.SUMMARY_ONLY, Outcome.APPEAL_TO_CC, Outcome.AQUITTED, null, null),
    SUMMARY_ONLY_11010140(11010140, Outcome.SUMMARY_ONLY, Outcome.APPEAL_TO_CC, Outcome.PART_CONVICTED, null, null),
    SUMMARY_ONLY_11010141(11010141, Outcome.SUMMARY_ONLY, Outcome.APPEAL_TO_CC, Outcome.CONVICTED, null, null),
    SUMMARY_ONLY_11010142(11010142, Outcome.SUMMARY_ONLY, Outcome.APPEAL_TO_CC, Outcome.DISMISSED, null, null),
    SUMMARY_ONLY_11010143(11010143, Outcome.SUMMARY_ONLY, Outcome.APPEAL_TO_CC, Outcome.PART_SUCCESS, null, null),
    SUMMARY_ONLY_11010144(11010144, Outcome.SUMMARY_ONLY, Outcome.APPEAL_TO_CC, Outcome.UNSUCCESSFUL, null, null),
    SUMMARY_ONLY_11010145(11010145, Outcome.SUMMARY_ONLY, Outcome.APPEAL_TO_CC, Outcome.SUCCESSFUL, null, null),
    SUMMARY_ONLY_11010146(11010146, Outcome.SUMMARY_ONLY, Outcome.APPEAL_TO_CC, Outcome.ABANDONED, null, null),
    CC_ALREADY_11010147(11010147, Outcome.CC_ALREADY, null, null, null, null),
    CC_ALREADY_11010148(11010148, Outcome.CC_ALREADY, Outcome.COMMITTED_FOR_TRIAL, null, null, null),
    CC_ALREADY_11010149(11010149, Outcome.CC_ALREADY, Outcome.COMMITTED_FOR_TRIAL, Outcome.AQUITTED, null, null),
    CC_ALREADY_11010150(11010150, Outcome.CC_ALREADY, Outcome.COMMITTED_FOR_TRIAL, Outcome.PART_CONVICTED, null, null),
    CC_ALREADY_11010151(11010151, Outcome.CC_ALREADY, Outcome.COMMITTED_FOR_TRIAL, Outcome.CONVICTED, null, null),
    CC_ALREADY_11010152(11010152, Outcome.CC_ALREADY, Outcome.COMMITTED_FOR_TRIAL, Outcome.DISMISSED, null, null),
    CC_ALREADY_11010153(11010153, Outcome.CC_ALREADY, Outcome.COMMITTED_FOR_TRIAL, Outcome.PART_SUCCESS, null, null),
    CC_ALREADY_11010154(11010154, Outcome.CC_ALREADY, Outcome.COMMITTED_FOR_TRIAL, Outcome.UNSUCCESSFUL, null, null),
    CC_ALREADY_11010155(11010155, Outcome.CC_ALREADY, Outcome.COMMITTED_FOR_TRIAL, Outcome.SUCCESSFUL, null, null),
    CC_ALREADY_11010156(11010156, Outcome.CC_ALREADY, Outcome.COMMITTED_FOR_TRIAL, Outcome.ABANDONED, null, null),
    CC_ALREADY_11010157(11010157, Outcome.CC_ALREADY, Outcome.SENT_FOR_TRIAL, null, null, null),
    CC_ALREADY_11010158(11010158, Outcome.CC_ALREADY, Outcome.SENT_FOR_TRIAL, Outcome.AQUITTED, null, null),
    CC_ALREADY_11010159(11010159, Outcome.CC_ALREADY, Outcome.SENT_FOR_TRIAL, Outcome.PART_CONVICTED, null, null),
    CC_ALREADY_11010160(11010160, Outcome.CC_ALREADY, Outcome.SENT_FOR_TRIAL, Outcome.CONVICTED, null, null),
    CC_ALREADY_11010161(11010161, Outcome.CC_ALREADY, Outcome.SENT_FOR_TRIAL, Outcome.DISMISSED, null, null),
    CC_ALREADY_11010162(11010162, Outcome.CC_ALREADY, Outcome.SENT_FOR_TRIAL, Outcome.PART_SUCCESS, null, null),
    CC_ALREADY_11010163(11010163, Outcome.CC_ALREADY, Outcome.SENT_FOR_TRIAL, Outcome.UNSUCCESSFUL, null, null),
    CC_ALREADY_11010164(11010164, Outcome.CC_ALREADY, Outcome.SENT_FOR_TRIAL, Outcome.SUCCESSFUL, null, null),
    CC_ALREADY_11010165(11010165, Outcome.CC_ALREADY, Outcome.SENT_FOR_TRIAL, Outcome.ABANDONED, null, null),
    CC_ALREADY_11010166(11010166, Outcome.CC_ALREADY, Outcome.RESOLVED_IN_MAGS, null, null, null),
    CC_ALREADY_11010167(11010167, Outcome.CC_ALREADY, Outcome.RESOLVED_IN_MAGS, Outcome.AQUITTED, null, null),
    CC_ALREADY_11010168(11010168, Outcome.CC_ALREADY, Outcome.RESOLVED_IN_MAGS, Outcome.PART_CONVICTED, null, null),
    CC_ALREADY_11010169(11010169, Outcome.CC_ALREADY, Outcome.RESOLVED_IN_MAGS, Outcome.CONVICTED, null, null),
    CC_ALREADY_11010170(11010170, Outcome.CC_ALREADY, Outcome.RESOLVED_IN_MAGS, Outcome.DISMISSED, null, null),
    CC_ALREADY_11010171(11010171, Outcome.CC_ALREADY, Outcome.RESOLVED_IN_MAGS, Outcome.PART_SUCCESS, null, null),
    CC_ALREADY_11010172(11010172, Outcome.CC_ALREADY, Outcome.RESOLVED_IN_MAGS, Outcome.UNSUCCESSFUL, null, null),
    CC_ALREADY_11010173(11010173, Outcome.CC_ALREADY, Outcome.RESOLVED_IN_MAGS, Outcome.SUCCESSFUL, null, null),
    CC_ALREADY_11010174(11010174, Outcome.CC_ALREADY, Outcome.RESOLVED_IN_MAGS, Outcome.ABANDONED, null, null),
    CC_ALREADY_11010175(11010175, Outcome.CC_ALREADY, Outcome.COMMITTED, null, null, null),
    CC_ALREADY_11010176(11010176, Outcome.CC_ALREADY, Outcome.COMMITTED, Outcome.AQUITTED, null, null),
    CC_ALREADY_11010177(11010177, Outcome.CC_ALREADY, Outcome.COMMITTED, Outcome.PART_CONVICTED, null, null),
    CC_ALREADY_11010178(11010178, Outcome.CC_ALREADY, Outcome.COMMITTED, Outcome.CONVICTED, null, null),
    CC_ALREADY_11010179(11010179, Outcome.CC_ALREADY, Outcome.COMMITTED, Outcome.DISMISSED, null, null),
    CC_ALREADY_11010180(11010180, Outcome.CC_ALREADY, Outcome.COMMITTED, Outcome.PART_SUCCESS, null, null),
    CC_ALREADY_11010181(11010181, Outcome.CC_ALREADY, Outcome.COMMITTED, Outcome.UNSUCCESSFUL, null, null),
    CC_ALREADY_11010182(11010182, Outcome.CC_ALREADY, Outcome.COMMITTED, Outcome.SUCCESSFUL, null, null),
    CC_ALREADY_11010183(11010183, Outcome.CC_ALREADY, Outcome.COMMITTED, Outcome.ABANDONED, null, null),
    CC_ALREADY_11010184(11010184, Outcome.CC_ALREADY, Outcome.APPEAL_TO_CC, null, null, null),
    CC_ALREADY_11010185(11010185, Outcome.CC_ALREADY, Outcome.APPEAL_TO_CC, Outcome.AQUITTED, null, null),
    CC_ALREADY_11010186(11010186, Outcome.CC_ALREADY, Outcome.APPEAL_TO_CC, Outcome.PART_CONVICTED, null, null),
    CC_ALREADY_11010187(11010187, Outcome.CC_ALREADY, Outcome.APPEAL_TO_CC, Outcome.CONVICTED, null, null),
    CC_ALREADY_11010188(11010188, Outcome.CC_ALREADY, Outcome.APPEAL_TO_CC, Outcome.DISMISSED, null, null),
    CC_ALREADY_11010189(11010189, Outcome.CC_ALREADY, Outcome.APPEAL_TO_CC, Outcome.PART_SUCCESS, null, null),
    CC_ALREADY_11010190(11010190, Outcome.CC_ALREADY, Outcome.APPEAL_TO_CC, Outcome.UNSUCCESSFUL, null, null),
    CC_ALREADY_11010191(11010191, Outcome.CC_ALREADY, Outcome.APPEAL_TO_CC, Outcome.SUCCESSFUL, null, null),
    CC_ALREADY_11010192(11010192, Outcome.CC_ALREADY, Outcome.APPEAL_TO_CC, Outcome.ABANDONED, null, null),
    APPEAL_CC_11010193(11010193, Outcome.APPEAL_CC, null, null, null, null),
    APPEAL_CC_11010194(11010194, Outcome.APPEAL_CC, Outcome.COMMITTED_FOR_TRIAL, null, null, null),
    APPEAL_CC_11010195(11010195, Outcome.APPEAL_CC, Outcome.COMMITTED_FOR_TRIAL, Outcome.AQUITTED, null, null),
    APPEAL_CC_11010196(11010196, Outcome.APPEAL_CC, Outcome.COMMITTED_FOR_TRIAL, Outcome.PART_CONVICTED, null, null),
    APPEAL_CC_11010197(11010197, Outcome.APPEAL_CC, Outcome.COMMITTED_FOR_TRIAL, Outcome.CONVICTED, null, null),
    APPEAL_CC_11010198(11010198, Outcome.APPEAL_CC, Outcome.COMMITTED_FOR_TRIAL, Outcome.DISMISSED, null, null),
    APPEAL_CC_11010199(11010199, Outcome.APPEAL_CC, Outcome.COMMITTED_FOR_TRIAL, Outcome.PART_SUCCESS, null, null),
    APPEAL_CC_11010200(11010200, Outcome.APPEAL_CC, Outcome.COMMITTED_FOR_TRIAL, Outcome.UNSUCCESSFUL, null, null),
    APPEAL_CC_11010201(11010201, Outcome.APPEAL_CC, Outcome.COMMITTED_FOR_TRIAL, Outcome.SUCCESSFUL, null, null),
    APPEAL_CC_11010202(11010202, Outcome.APPEAL_CC, Outcome.COMMITTED_FOR_TRIAL, Outcome.ABANDONED, null, null),
    APPEAL_CC_11010203(11010203, Outcome.APPEAL_CC, Outcome.SENT_FOR_TRIAL, null, null, null),
    APPEAL_CC_11010204(11010204, Outcome.APPEAL_CC, Outcome.SENT_FOR_TRIAL, Outcome.AQUITTED, null, null),
    APPEAL_CC_11010205(11010205, Outcome.APPEAL_CC, Outcome.SENT_FOR_TRIAL, Outcome.PART_CONVICTED, null, null),
    APPEAL_CC_11010206(11010206, Outcome.APPEAL_CC, Outcome.SENT_FOR_TRIAL, Outcome.CONVICTED, null, null),
    APPEAL_CC_11010207(11010207, Outcome.APPEAL_CC, Outcome.SENT_FOR_TRIAL, Outcome.DISMISSED, null, null),
    APPEAL_CC_11010212(11010212, Outcome.APPEAL_CC, Outcome.RESOLVED_IN_MAGS, null, null, null),
    APPEAL_CC_11010208(11010208, Outcome.APPEAL_CC, Outcome.SENT_FOR_TRIAL, Outcome.PART_SUCCESS, null, null),
    APPEAL_CC_11010209(11010209, Outcome.APPEAL_CC, Outcome.SENT_FOR_TRIAL, Outcome.UNSUCCESSFUL, null, null),
    APPEAL_CC_11010210(11010210, Outcome.APPEAL_CC, Outcome.SENT_FOR_TRIAL, Outcome.SUCCESSFUL, null, null),
    APPEAL_CC_11010211(11010211, Outcome.APPEAL_CC, Outcome.SENT_FOR_TRIAL, Outcome.ABANDONED, null, null),
    APPEAL_CC_11010213(11010213, Outcome.APPEAL_CC, Outcome.RESOLVED_IN_MAGS, Outcome.AQUITTED, null, null),
    APPEAL_CC_11010214(11010214, Outcome.APPEAL_CC, Outcome.RESOLVED_IN_MAGS, Outcome.PART_CONVICTED, null, null),
    APPEAL_CC_11010215(11010215, Outcome.APPEAL_CC, Outcome.RESOLVED_IN_MAGS, Outcome.CONVICTED, null, null),
    APPEAL_CC_11010216(11010216, Outcome.APPEAL_CC, Outcome.RESOLVED_IN_MAGS, Outcome.DISMISSED, null, null),
    APPEAL_CC_11010217(11010217, Outcome.APPEAL_CC, Outcome.RESOLVED_IN_MAGS, Outcome.PART_SUCCESS, null, null),
    APPEAL_CC_11010218(11010218, Outcome.APPEAL_CC, Outcome.RESOLVED_IN_MAGS, Outcome.UNSUCCESSFUL, null, null),
    APPEAL_CC_11010219(11010219, Outcome.APPEAL_CC, Outcome.RESOLVED_IN_MAGS, Outcome.SUCCESSFUL, null, null),
    APPEAL_CC_11010220(11010220, Outcome.APPEAL_CC, Outcome.RESOLVED_IN_MAGS, Outcome.ABANDONED, null, null),
    APPEAL_CC_11010221(11010221, Outcome.APPEAL_CC, Outcome.COMMITTED, null, null, null),
    APPEAL_CC_11010222(11010222, Outcome.APPEAL_CC, Outcome.COMMITTED, Outcome.AQUITTED, null, null),
    APPEAL_CC_11010223(11010223, Outcome.APPEAL_CC, Outcome.COMMITTED, Outcome.PART_CONVICTED, null, null),
    APPEAL_CC_11010224(11010224, Outcome.APPEAL_CC, Outcome.COMMITTED, Outcome.CONVICTED, null, null),
    APPEAL_CC_11010225(11010225, Outcome.APPEAL_CC, Outcome.COMMITTED, Outcome.DISMISSED, null, null),
    APPEAL_CC_11010226(11010226, Outcome.APPEAL_CC, Outcome.COMMITTED, Outcome.PART_SUCCESS, null, null),
    APPEAL_CC_11010227(11010227, Outcome.APPEAL_CC, Outcome.COMMITTED, Outcome.UNSUCCESSFUL, null, null),
    APPEAL_CC_11010228(11010228, Outcome.APPEAL_CC, Outcome.COMMITTED, Outcome.SUCCESSFUL, null, null),
    APPEAL_CC_11010229(11010229, Outcome.APPEAL_CC, Outcome.COMMITTED, Outcome.ABANDONED, null, null),
    APPEAL_CC_11010230(11010230, Outcome.APPEAL_CC, Outcome.APPEAL_TO_CC, null, null, null),
    APPEAL_CC_11010231(11010231, Outcome.APPEAL_CC, Outcome.APPEAL_TO_CC, Outcome.AQUITTED, null, null),
    APPEAL_CC_11010232(11010232, Outcome.APPEAL_CC, Outcome.APPEAL_TO_CC, Outcome.PART_CONVICTED, null, null),
    APPEAL_CC_11010233(11010233, Outcome.APPEAL_CC, Outcome.APPEAL_TO_CC, Outcome.CONVICTED, null, null),
    APPEAL_CC_11010234(11010234, Outcome.APPEAL_CC, Outcome.APPEAL_TO_CC, Outcome.DISMISSED, null, null),
    APPEAL_CC_11010235(11010235, Outcome.APPEAL_CC, Outcome.APPEAL_TO_CC, Outcome.PART_SUCCESS, null, null),
    APPEAL_CC_11010236(11010236, Outcome.APPEAL_CC, Outcome.APPEAL_TO_CC, Outcome.UNSUCCESSFUL, null, null),
    APPEAL_CC_11010237(11010237, Outcome.APPEAL_CC, Outcome.APPEAL_TO_CC, Outcome.SUCCESSFUL, null, null),
    APPEAL_CC_11010238(11010238, Outcome.APPEAL_CC, Outcome.APPEAL_TO_CC, Outcome.ABANDONED, null, null),
    COMMITAL_11010239(11010239, Outcome.COMMITAL, null, null, null, null),
    COMMITAL_11010240(11010240, Outcome.COMMITAL, Outcome.COMMITTED_FOR_TRIAL, null, null, null),
    COMMITAL_11010241(11010241, Outcome.COMMITAL, Outcome.COMMITTED_FOR_TRIAL, Outcome.AQUITTED, null, null),
    COMMITAL_11010242(11010242, Outcome.COMMITAL, Outcome.COMMITTED_FOR_TRIAL, Outcome.PART_CONVICTED, null, null),
    COMMITAL_11010243(11010243, Outcome.COMMITAL, Outcome.COMMITTED_FOR_TRIAL, Outcome.CONVICTED, null, null),
    COMMITAL_11010244(11010244, Outcome.COMMITAL, Outcome.COMMITTED_FOR_TRIAL, Outcome.DISMISSED, null, null),
    COMMITAL_11010245(11010245, Outcome.COMMITAL, Outcome.COMMITTED_FOR_TRIAL, Outcome.PART_SUCCESS, null, null),
    COMMITAL_11010246(11010246, Outcome.COMMITAL, Outcome.COMMITTED_FOR_TRIAL, Outcome.UNSUCCESSFUL, null, null),
    COMMITAL_11010247(11010247, Outcome.COMMITAL, Outcome.COMMITTED_FOR_TRIAL, Outcome.SUCCESSFUL, null, null),
    COMMITAL_11010248(11010248, Outcome.COMMITAL, Outcome.COMMITTED_FOR_TRIAL, Outcome.ABANDONED, null, null),
    COMMITAL_11010249(11010249, Outcome.COMMITAL, Outcome.SENT_FOR_TRIAL, null, null, null),
    COMMITAL_11010250(11010250, Outcome.COMMITAL, Outcome.SENT_FOR_TRIAL, Outcome.AQUITTED, null, null),
    COMMITAL_11010251(11010251, Outcome.COMMITAL, Outcome.SENT_FOR_TRIAL, Outcome.PART_CONVICTED, null, null),
    COMMITAL_11010252(11010252, Outcome.COMMITAL, Outcome.SENT_FOR_TRIAL, Outcome.CONVICTED, null, null),
    COMMITAL_11010253(11010253, Outcome.COMMITAL, Outcome.SENT_FOR_TRIAL, Outcome.DISMISSED, null, null),
    COMMITAL_11010254(11010254, Outcome.COMMITAL, Outcome.SENT_FOR_TRIAL, Outcome.PART_SUCCESS, null, null),
    COMMITAL_11010255(11010255, Outcome.COMMITAL, Outcome.SENT_FOR_TRIAL, Outcome.UNSUCCESSFUL, null, null),
    COMMITAL_11010256(11010256, Outcome.COMMITAL, Outcome.SENT_FOR_TRIAL, Outcome.SUCCESSFUL, null, null),
    COMMITAL_11010257(11010257, Outcome.COMMITAL, Outcome.SENT_FOR_TRIAL, Outcome.ABANDONED, null, null),
    COMMITAL_11010258(11010258, Outcome.COMMITAL, Outcome.RESOLVED_IN_MAGS, null, null, null),
    COMMITAL_11010259(11010259, Outcome.COMMITAL, Outcome.RESOLVED_IN_MAGS, Outcome.AQUITTED, null, null),
    COMMITAL_11010260(11010260, Outcome.COMMITAL, Outcome.RESOLVED_IN_MAGS, Outcome.PART_CONVICTED, null, null),
    COMMITAL_11010261(11010261, Outcome.COMMITAL, Outcome.RESOLVED_IN_MAGS, Outcome.CONVICTED, null, null),
    COMMITAL_11010262(11010262, Outcome.COMMITAL, Outcome.RESOLVED_IN_MAGS, Outcome.DISMISSED, null, null),
    COMMITAL_11010263(11010263, Outcome.COMMITAL, Outcome.RESOLVED_IN_MAGS, Outcome.PART_SUCCESS, null, null),
    COMMITAL_11010264(11010264, Outcome.COMMITAL, Outcome.RESOLVED_IN_MAGS, Outcome.UNSUCCESSFUL, null, null),
    COMMITAL_11010265(11010265, Outcome.COMMITAL, Outcome.RESOLVED_IN_MAGS, Outcome.SUCCESSFUL, null, null),
    COMMITAL_11010266(11010266, Outcome.COMMITAL, Outcome.RESOLVED_IN_MAGS, Outcome.ABANDONED, null, null),
    COMMITAL_11010267(11010267, Outcome.COMMITAL, Outcome.COMMITTED, null, null, null),
    COMMITAL_11010268(11010268, Outcome.COMMITAL, Outcome.COMMITTED, Outcome.AQUITTED, null, null),
    COMMITAL_11010269(11010269, Outcome.COMMITAL, Outcome.COMMITTED, Outcome.PART_CONVICTED, null, null),
    COMMITAL_11010270(11010270, Outcome.COMMITAL, Outcome.COMMITTED, Outcome.CONVICTED, null, null),
    COMMITAL_11010271(11010271, Outcome.COMMITAL, Outcome.COMMITTED, Outcome.DISMISSED, null, null),
    COMMITAL_11010272(11010272, Outcome.COMMITAL, Outcome.COMMITTED, Outcome.PART_SUCCESS, null, null),
    COMMITAL_11010273(11010273, Outcome.COMMITAL, Outcome.COMMITTED, Outcome.UNSUCCESSFUL, null, null),
    COMMITAL_11010274(11010274, Outcome.COMMITAL, Outcome.COMMITTED, Outcome.SUCCESSFUL, null, null),
    COMMITAL_11010275(11010275, Outcome.COMMITAL, Outcome.COMMITTED, Outcome.ABANDONED, null, null),
    COMMITAL_11010276(11010276, Outcome.COMMITAL, Outcome.APPEAL_TO_CC, null, null, null),
    COMMITAL_11010277(11010277, Outcome.COMMITAL, Outcome.APPEAL_TO_CC, Outcome.AQUITTED, null, null),
    COMMITAL_11010278(11010278, Outcome.COMMITAL, Outcome.APPEAL_TO_CC, Outcome.PART_CONVICTED, null, null),
    COMMITAL_11010279(11010279, Outcome.COMMITAL, Outcome.APPEAL_TO_CC, Outcome.CONVICTED, null, null),
    COMMITAL_11010280(11010280, Outcome.COMMITAL, Outcome.APPEAL_TO_CC, Outcome.DISMISSED, null, null),
    COMMITAL_11010281(11010281, Outcome.COMMITAL, Outcome.APPEAL_TO_CC, Outcome.PART_SUCCESS, null, null),
    COMMITAL_11010282(11010282, Outcome.COMMITAL, Outcome.APPEAL_TO_CC, Outcome.UNSUCCESSFUL, null, null),
    COMMITAL_11010283(11010283, Outcome.COMMITAL, Outcome.APPEAL_TO_CC, Outcome.SUCCESSFUL, null, null),
    COMMITAL_11010284(11010284, Outcome.COMMITAL, Outcome.APPEAL_TO_CC, Outcome.ABANDONED, null, null),
    EITHER_WAY_11010285(11010285, Outcome.EITHER_WAY, null, null, Outcome.SOL_COSTS, Outcome.PLUS),
    EITHER_WAY_11010286(11010286, Outcome.EITHER_WAY, Outcome.COMMITTED_FOR_TRIAL, null, Outcome.SOL_COSTS, Outcome.PLUS),
    EITHER_WAY_11010287(11010287, Outcome.EITHER_WAY, Outcome.COMMITTED_FOR_TRIAL, Outcome.AQUITTED, null, null),
    EITHER_WAY_11010288(11010288, Outcome.EITHER_WAY, Outcome.COMMITTED_FOR_TRIAL, Outcome.PART_CONVICTED, null, null),
    EITHER_WAY_11010289(11010289, Outcome.EITHER_WAY, Outcome.COMMITTED_FOR_TRIAL, Outcome.CONVICTED, null, null),
    EITHER_WAY_11010290(11010290, Outcome.EITHER_WAY, Outcome.COMMITTED_FOR_TRIAL, Outcome.DISMISSED, null, null),
    EITHER_WAY_11010291(11010291, Outcome.EITHER_WAY, Outcome.COMMITTED_FOR_TRIAL, Outcome.PART_SUCCESS, null, null),
    EITHER_WAY_11010292(11010292, Outcome.EITHER_WAY, Outcome.COMMITTED_FOR_TRIAL, Outcome.UNSUCCESSFUL, null, null),
    EITHER_WAY_11010293(11010293, Outcome.EITHER_WAY, Outcome.COMMITTED_FOR_TRIAL, Outcome.SUCCESSFUL, null, null),
    EITHER_WAY_11010294(11010294, Outcome.EITHER_WAY, Outcome.COMMITTED_FOR_TRIAL, Outcome.ABANDONED, null, null),
    EITHER_WAY_11010295(11010295, Outcome.EITHER_WAY, Outcome.SENT_FOR_TRIAL, null, Outcome.SOL_COSTS, Outcome.PLUS),
    EITHER_WAY_11010296(11010296, Outcome.EITHER_WAY, Outcome.SENT_FOR_TRIAL, Outcome.AQUITTED, null, null),
    EITHER_WAY_11010297(11010297, Outcome.EITHER_WAY, Outcome.SENT_FOR_TRIAL, Outcome.PART_CONVICTED, null, null),
    EITHER_WAY_11010298(11010298, Outcome.EITHER_WAY, Outcome.SENT_FOR_TRIAL, Outcome.CONVICTED, null, null),
    EITHER_WAY_11010299(11010299, Outcome.EITHER_WAY, Outcome.SENT_FOR_TRIAL, Outcome.DISMISSED, null, null),
    EITHER_WAY_11010300(11010300, Outcome.EITHER_WAY, Outcome.SENT_FOR_TRIAL, Outcome.PART_SUCCESS, null, null),
    EITHER_WAY_11010301(11010301, Outcome.EITHER_WAY, Outcome.SENT_FOR_TRIAL, Outcome.UNSUCCESSFUL, null, null),
    EITHER_WAY_11010302(11010302, Outcome.EITHER_WAY, Outcome.SENT_FOR_TRIAL, Outcome.SUCCESSFUL, null, null),
    EITHER_WAY_11010303(11010303, Outcome.EITHER_WAY, Outcome.SENT_FOR_TRIAL, Outcome.ABANDONED, null, null),
    EITHER_WAY_11010304(11010304, Outcome.EITHER_WAY, Outcome.RESOLVED_IN_MAGS, null, null, null),
    EITHER_WAY_11010305(11010305, Outcome.EITHER_WAY, Outcome.RESOLVED_IN_MAGS, Outcome.AQUITTED, null, null),
    EITHER_WAY_11010306(11010306, Outcome.EITHER_WAY, Outcome.RESOLVED_IN_MAGS, Outcome.PART_CONVICTED, null, null),
    EITHER_WAY_11010307(11010307, Outcome.EITHER_WAY, Outcome.RESOLVED_IN_MAGS, Outcome.CONVICTED, null, null),
    EITHER_WAY_11010308(11010308, Outcome.EITHER_WAY, Outcome.RESOLVED_IN_MAGS, Outcome.DISMISSED, null, null),
    EITHER_WAY_11010309(11010309, Outcome.EITHER_WAY, Outcome.RESOLVED_IN_MAGS, Outcome.PART_SUCCESS, null, null),
    EITHER_WAY_11010310(11010310, Outcome.EITHER_WAY, Outcome.RESOLVED_IN_MAGS, Outcome.UNSUCCESSFUL, null, null),
    EITHER_WAY_11010311(11010311, Outcome.EITHER_WAY, Outcome.RESOLVED_IN_MAGS, Outcome.SUCCESSFUL, null, null),
    EITHER_WAY_11010312(11010312, Outcome.EITHER_WAY, Outcome.RESOLVED_IN_MAGS, Outcome.ABANDONED, null, null),
    EITHER_WAY_11010313(11010313, Outcome.EITHER_WAY, Outcome.COMMITTED, null, Outcome.SOL_COSTS, Outcome.PLUS),
    EITHER_WAY_11010314(11010314, Outcome.EITHER_WAY, Outcome.COMMITTED, Outcome.AQUITTED, null, null),
    EITHER_WAY_11010315(11010315, Outcome.EITHER_WAY, Outcome.COMMITTED, Outcome.PART_CONVICTED, null, null),
    EITHER_WAY_11010316(11010316, Outcome.EITHER_WAY, Outcome.COMMITTED, Outcome.CONVICTED, null, null),
    EITHER_WAY_11010317(11010317, Outcome.EITHER_WAY, Outcome.COMMITTED, Outcome.DISMISSED, null, null),
    EITHER_WAY_11010318(11010318, Outcome.EITHER_WAY, Outcome.COMMITTED, Outcome.PART_SUCCESS, null, null),
    EITHER_WAY_11010319(11010319, Outcome.EITHER_WAY, Outcome.COMMITTED, Outcome.UNSUCCESSFUL, null, null),
    EITHER_WAY_11010320(11010320, Outcome.EITHER_WAY, Outcome.COMMITTED, Outcome.SUCCESSFUL, null, null),
    EITHER_WAY_11010321(11010321, Outcome.EITHER_WAY, Outcome.COMMITTED, Outcome.ABANDONED, null, null),
    EITHER_WAY_11010322(11010322, Outcome.EITHER_WAY, Outcome.APPEAL_TO_CC, null, Outcome.SOL_COSTS, Outcome.PLUS),
    EITHER_WAY_11010323(11010323, Outcome.EITHER_WAY, Outcome.APPEAL_TO_CC, Outcome.AQUITTED, null, null),
    EITHER_WAY_11010324(11010324, Outcome.EITHER_WAY, Outcome.APPEAL_TO_CC, Outcome.PART_CONVICTED, null, null),
    EITHER_WAY_11010325(11010325, Outcome.EITHER_WAY, Outcome.APPEAL_TO_CC, Outcome.CONVICTED, null, null),
    EITHER_WAY_11010326(11010326, Outcome.EITHER_WAY, Outcome.APPEAL_TO_CC, Outcome.DISMISSED, null, null),
    EITHER_WAY_11010327(11010327, Outcome.EITHER_WAY, Outcome.APPEAL_TO_CC, Outcome.PART_SUCCESS, null, null),
    EITHER_WAY_11010328(11010328, Outcome.EITHER_WAY, Outcome.APPEAL_TO_CC, Outcome.UNSUCCESSFUL, null, null),
    EITHER_WAY_11010329(11010329, Outcome.EITHER_WAY, Outcome.APPEAL_TO_CC, Outcome.SUCCESSFUL, null, null),
    EITHER_WAY_11010330(11010330, Outcome.EITHER_WAY, Outcome.APPEAL_TO_CC, Outcome.ABANDONED, null, null);

    private final Integer id;
    private final String caseType;
    private final String magistratesCourtOutcome;
    private final String crownCourtOutcome;
    private final String variation;
    private final String variationRule;



    public static ContributionRules getFrom(Integer id) {
        if (null == id) return null;

        return Stream.of(ContributionRules.values())
                .filter(correspondenceRules-> correspondenceRules.id.equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Contribution Rules Id with value: %s does not exist.", id)));
    }


    private static class Outcome {
        private static final String EITHER_WAY = "EITHER WAY";
        private static final String COMMITAL = "COMMITAL";
        private static final String COMMITTED = "COMMITTED";
        private static final String APPEAL_CC = "APPEAL CC";
        private static final String CC_ALREADY = "CC ALREADY";
        private static final String SUMMARY_ONLY = "SUMMARY ONLY";
        private static final String INDICTABLE = "INDICTABLE";
        private static final String COMMITTED_FOR_TRIAL = "COMMITTED FOR TRIAL";
        private static final String SENT_FOR_TRIAL = "SENT FOR TRIAL";
        private static final String RESOLVED_IN_MAGS = "RESOLVED IN MAGS";
        private static final String APPEAL_TO_CC = "APPEAL TO CC";
        private static final String ABANDONED = "ABANDONED";
        private static final String SUCCESSFUL = "SUCCESSFUL";
        private static final String UNSUCCESSFUL = "UNSUCCESSFUL";
        private static final String PART_SUCCESS = "PART SUCCESS";
        private static final String DISMISSED = "DISMISSED";
        private static final String CONVICTED = "CONVICTED";
        private static final String PART_CONVICTED = "PART CONVICTED";
        private static final String AQUITTED = "AQUITTED";

        private static final String SOL_COSTS = "SOL COSTS";

        private static final String PLUS = "+";

    }

}


