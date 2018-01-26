package self.ec.genomics;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RandomVariantGenerator {

  public static class Variant {
    public String sampleId;
    public String chr;
    public int pos;
    public String ref;
    public String alt;
  }

  private static final Random random = new Random();

  private static final ObjectMapper om = new ObjectMapper();

  private static final String[] bases = new String[] {"A", "T", "C", "G"};

  private static final String[] samples = new String[] {"SA-1", "SA-2", "SA-3", "SA-4", "SA-5"};

  public static void main(String[] args) throws Exception {
    try (PrintWriter out = new PrintWriter("/Users/emre/tmp/variants.json")) {
      for (String sample : samples) {
        List<Variant> variants = generateRandomVariants(sample, 1000);
        for (Variant v : variants) {
          out.println(om.writeValueAsString(v));
        }
      }
    }
  }

  private static List<Variant> generateRandomVariants(String sampleId, int num) {
    List<Variant> variants = new ArrayList<>(num);
    int pos = 1000000;
    for (int i = 0; i < num; i++) {
      Variant v = generateRandomVariant(sampleId, pos);
      variants.add(v);
      pos = v.pos;
    }
    
    Collections.sort(variants, new Comparator<Variant>() {
      @Override
      public int compare(Variant v1, Variant v2) {
        int chrComp = Integer.parseInt(v1.chr) - Integer.parseInt(v2.chr);
        if (chrComp != 0) {
          return chrComp;
        }      
        return v1.pos - v2.pos;
      }      
    });
    
    return variants;
  }

  private static Variant generateRandomVariant(String sampleId, int prevPos) {
    Variant v = new Variant();
    v.sampleId = sampleId;
    v.chr = String.valueOf(1 + random.nextInt(21));
    v.pos = prevPos + 1 + random.nextInt(1000000);
    int refIdx = random.nextInt(bases.length);
    int altIdx = (refIdx + 1 + random.nextInt(2)) % 4;
    v.ref = bases[refIdx];
    v.alt = bases[altIdx];
    return v;
  }

}
