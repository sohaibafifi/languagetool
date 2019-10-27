#!/usr/bin/perl

use warnings;
use strict;
use utf8;

binmode(STDIN, ":utf8");
binmode(STDOUT, ":utf8");
binmode(STDERR, ":utf8");

my %TOKEN = (
    '<N>UNLENITED</N>' => '<token postag=".*Noun.*" postag_regexp="yes"><exception postag="*:Len" postag_regexp="yes"/></token>',
    '<NG>UNLENITED</NG>' => '<token postag=".*Noun.*:Gen.*" postag_regexp="yes"><exception postag="*:Len" postag_regexp="yes"/></token>',
    '<A>UNLENITED</A>' => '<token postag="Adj:.*" postag_regexp="yes"><exception postag="*:Len" postag_regexp="yes"/></token>',
    '<N pl="n" gnt="n" gnd="f">ECLIPSED</N>' => '<token postag="(?:C[UMC]:)?Noun:Fem:Com:Sg:Ecl" postag_regexp="yes"></token>',
);

my %PARTTOKEN = (
    '<V cop="y">' => 'Cop:.*',
    '<N pl="n" gnt="n" gnd="f">' => '(?:C[UMC]:)?Noun:Fem:Com:Sg',
    '<N pl="n" gnt="n" gnd="m">' => '(?:C[UMC]:)?Noun:Masc:Com:Sg',
    '<N pl="n" gnt="y" gnd="f">' => '(?:C[UMC]:)?Noun:Fem:Gen:Sg',
    '<N pl="n" gnt="y" gnd="m">' => '(?:C[UMC]:)?Noun:Masc:Gen:Sg',
    '<N pl="y" gnt="n" gnd="f">' => '(?:C[UMC]:)?Noun:Fem:Com:Pl',
    '<N pl="y" gnt="n" gnd="m">' => '(?:C[UMC]:)?Noun:Masc:Com:Pl',
    '<N pl="y" gnt="y" gnd="f">' => '(?:C[UMC]:)?Noun:Fem:Gen:Pl',
    '<N pl="y" gnt="y" gnd="m">' => '(?:C[UMC]:)?Noun:Masc:Gen:Pl',
);

my %POS = (
    'A' => 'Adj:.*',
    'N' => '.*Noun.*',
    'NG' => '.*Noun.*:Gen.*',
    'NFCS' => '.*Noun:Fem:Com:Sg',
    'NMCS' => '.*Noun:Masc:Com:Sg',
    'NCS' => '.*Noun:(?:Fem|Masc):Com:Sg',
    'NFGS' => '.*Noun:Fem:Gen:Sg',
    'NMGS' => '.*Noun:Masc:Gen:Sg',
    'NP' => '.*Noun:.*:Pl',
);

my @mentities = qq/
lenitedfuture
abairpast
abairprfu
faighfc
abspastverb
absnonpastverb
dependent
justta
justata
nonrformconj
nonrformprep
rformconj
rformprep
ahnumber
cheadcompound
noncompound
compound
synthpast
allgenitivepreps
genitiveprep
irregularpast
pastnorformlen
pastnorform
pastafterni
faigheclipsed
faightoeclipse
positiveint
twotonineteen
vowelnumeral
vowelordinal
vowelnumadj
nibs
unleniteds
initialvowelorf
initialvowel
initialconsonant
nonvowelnonf
uneclipseddt
uneclipsedcons
uneclipsed
eclipsedvowel
eclipseddt
eclipsedbcfgp
eclipsed
maybeeclipsingnumber
eclipsingnumber
eclipsingposs
unboundadj
unpplike
fakepp
unlenitable
unlenitedbcgmp
unlenitedbcfgmp
unmutatedvnish
unmutatedbcfgp
unmutated
unlenitedf
unlenitedcdfgst
unlenited
lenitedf
ordinaladj
prefixedt
eire
lenitedngmperson
lenitedngfperson
ngmperson
ngfperson
regularposs
fusedposs
fusedprep
dativeprep
initialmordapost
initialbigdapost
initialdapostf
initialdapost
initialbapost
initialmbapost
lenitedcapcg
lenitedbcfgmps
lenitedbmp
mutateddst
leniteddfst
leniteddst
lenited
slenderfinalconsonant
broadfinalconsonant
finalvowel
finala
slenderfinaldlnst
finaldlnst
dayoftheweek
nobeeapost
femvn
subjectpronoun
notvnishunlen
notvnishvn
vnish
femabstractrestricted
femabstract
quantityword
doword
arword
initialc
initialdst
initialf
initialh
initiall
initialm
initialn
initials
initialts
notna
broadfirstpres
slenderfirstpres
broadsecondpres
slendersecondpres
broadfirstfuture
slenderfirstfuture
broadsecondfuture
slendersecondfuture
broadfirstcond
slenderfirstcond
broadsecondcond
slendersecondcond
broadfirstimp
slenderfirstimp
broadsecondimp
slendersecondimp
/;

my %entities = map { $_ => 1 } @mentities;

my %msg = (
    'SEIMHIU' => 'Séimhiú ar iarraidh: ',
);

my @examples = ();
my @tokens = ();
while(<>) {
	chomp;
	s/\[Aa\]/a/g;
	s/\[Áá\]/á/g;
	s/\[Bb\]/b/g;
	s/\[Cc\]/c/g;
	s/\[Dd\]/d/g;
	s/\[Ee\]/e/g;
	s/\[Ff\]/f/g;
	s/\[Gg\]/g/g;
	s/\[Hh\]/h/g;
	s/\[Ii\]/i/g;
	s/\[Ll\]/l/g;
	s/\[Mm\]/m/g;
	s/\[Nn\]/n/g;
	s/\[Oo\]/o/g;
	s/\[Óó\]/ó/g;
	s/\[Pp\]/p/g;
	s/\[Rr\]/r/g;
	s/\[Ss\]/s/g;
	s/\[Tt\]/t/g;
	s/\[Uu\]/u/g;
	s/\[Úú\]/ú/g;

    if(/^#\. (.*)$/) {
        push @examples, $1;
    }
    next if(/^#/);
	if(/(.*):BACHOIR\{([^\}]+)\}$/) {
		my $match = $1;
		my $repl = $2;

        # save for later, in case
        my $line = $_;
print STDERR "In! $line\n";
        $match =~ s/> ([A-Z]+) </> <X>$1<\/X> </g;
        while(/(<[^>]*>[^<]*<[^>]*>)/) {
            my $rawtoken = $1;
            if(exists $TOKEN{$rawtoken}) {
                my $token = $TOKEN{$rawtoken};
                push @tokens, $token;
                next;
            } elsif ($rawtoken =~ /(<([^ ]*)[^>]*>)([^<]*)<[^>]>/) {
                my $rawfulltok = $1;
                my $postag = $2;
                my $inner = $3;
                my $tokout = '<token';
                if(exists $PARTTOKEN{$rawfulltok}) {
                    $tokout .= " postag=\"$PARTTOKEN{$rawfulltok}\" postag_regexp=\"yes\"";
                } elsif(exists $POS{$postag}) {
                    $tokout .= " postag=\"$POS{$rawfulltok}\" postag_regexp=\"yes\"";
                } elsif($postag eq 'X') {
                    # Do nothing
                } else {
                    print STDERR "WARNING: unknown POS tag $postag\n";
                }
                if($inner eq 'ANYTHING') {
                    $tokout .= '></token>';
                    push @tokens, $tokout;
                    next;
                } elsif($inner =~ /[A-Z][A-Z]+/) {
                    if(exists $entities{lc($inner)}) {
                        $tokout .= " regexp=\"yes\">&" . lc($inner) .";</token>";
                        push @tokens, $tokout;
                        next;
                    } else {
                        print STDERR "CONVERSION_FAILURE: $line\n";
                    }
                } else {
                    if($inner =~ /[\?\|\(]/) {
                        $tokout .= " regexp=\"yes\">$inner</token>";
                        push @tokens, $tokout;
                        next;
                    } else {
                        $tokout .= ">$inner</token>";
                        push @tokens, $tokout;
                        next;
                    }
                }
                die "Shouldn't get here";
            }
        }
        for my $tok (@tokens) {
            print "$tok\n";
        }
        @tokens = ();
        for my $eg (@examples) {
            print "$eg\n";
        }
        @examples = ();
    }
}
