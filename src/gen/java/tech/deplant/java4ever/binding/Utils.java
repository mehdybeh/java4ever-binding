package tech.deplant.java4ever.binding;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.Boolean;
import java.lang.Long;
import java.lang.String;

/**
 * <strong>Utils</strong>
 * Contains methods of "utils" module of EVER-SDK API
 *
 * Misc utility Functions. 
 * @version 1.43.3
 */
public final class Utils {
  /**
   *  Converts address from any TON format to any TON format
   *
   * @param address  Account address in any TON format.
   * @param outputFormat  Specify the format to convert to.
   */
  public static Utils.ResultOfConvertAddress convertAddress(EverSdkContext ctx, String address,
      Utils.AddressStringFormat outputFormat) throws EverSdkException {
    return ctx.call("utils.convert_address", new Utils.ParamsOfConvertAddress(address, outputFormat), Utils.ResultOfConvertAddress.class);
  }

  /**
   * Address types are the following
   *
   * `0:919db8e740d50bf349df2eea03fa30c385d846b991ff5542e67098ee833fc7f7` - standard TON address most
   * commonly used in all cases. Also called as hex address
   * `919db8e740d50bf349df2eea03fa30c385d846b991ff5542e67098ee833fc7f7` - account ID. A part of full
   * address. Identifies account inside particular workchain
   * `EQCRnbjnQNUL80nfLuoD+jDDhdhGuZH/VULmcJjugz/H9wam` - base64 address. Also called "user-friendly".
   * Was used at the beginning of TON. Now it is supported for compatibility Validates and returns the type of any TON address.
   *
   * @param address  Account address in any TON format.
   */
  public static Utils.ResultOfGetAddressType getAddressType(EverSdkContext ctx, String address)
      throws EverSdkException {
    return ctx.call("utils.get_address_type", new Utils.ParamsOfGetAddressType(address), Utils.ResultOfGetAddressType.class);
  }

  /**
   *  Calculates storage fee for an account over a specified time period
   */
  public static Utils.ResultOfCalcStorageFee calcStorageFee(EverSdkContext ctx, String account,
      Long period) throws EverSdkException {
    return ctx.call("utils.calc_storage_fee", new Utils.ParamsOfCalcStorageFee(account, period), Utils.ResultOfCalcStorageFee.class);
  }

  /**
   *  Compresses data using Zstandard algorithm
   *
   * @param uncompressed Must be encoded as base64. Uncompressed data.
   * @param level  Compression level, from 1 to 21. Where: 1 - lowest compression level (fastest compression); 21 - highest compression level (slowest compression). If level is omitted, the default compression level is used (currently `3`).
   */
  public static Utils.ResultOfCompressZstd compressZstd(EverSdkContext ctx, String uncompressed,
      Long level) throws EverSdkException {
    return ctx.call("utils.compress_zstd", new Utils.ParamsOfCompressZstd(uncompressed, level), Utils.ResultOfCompressZstd.class);
  }

  /**
   *  Decompresses data using Zstandard algorithm
   *
   * @param compressed Must be encoded as base64. Compressed data.
   */
  public static Utils.ResultOfDecompressZstd decompressZstd(EverSdkContext ctx, String compressed)
      throws EverSdkException {
    return ctx.call("utils.decompress_zstd", new Utils.ParamsOfDecompressZstd(compressed), Utils.ResultOfDecompressZstd.class);
  }

  public static final record ParamsOfCalcStorageFee(String account, Long period) {
  }

  public static final record ResultOfCalcStorageFee(String fee) {
  }

  /**
   * @param compressed Must be encoded as base64. Compressed data.
   */
  public static final record ParamsOfDecompressZstd(String compressed) {
  }

  /**
   * @param addressType  Account address type.
   */
  public static final record ResultOfGetAddressType(Utils.AccountAddressType addressType) {
  }

  public enum AccountAddressType {
    AccountId,

    Hex,

    Base64
  }

  /**
   * @param address  Account address in any TON format.
   * @param outputFormat  Specify the format to convert to.
   */
  public static final record ParamsOfConvertAddress(String address,
      Utils.AddressStringFormat outputFormat) {
  }

  /**
   * @param uncompressed Must be encoded as base64. Uncompressed data.
   * @param level  Compression level, from 1 to 21. Where: 1 - lowest compression level (fastest compression); 21 - highest compression level (slowest compression). If level is omitted, the default compression level is used (currently `3`).
   */
  public static final record ParamsOfCompressZstd(String uncompressed, Long level) {
  }

  /**
   * @param decompressed Must be encoded as base64. Decompressed data.
   */
  public static final record ResultOfDecompressZstd(String decompressed) {
  }

  /**
   * @param address  Address in the specified format
   */
  public static final record ResultOfConvertAddress(String address) {
  }

  public sealed interface AddressStringFormat {
    final record AccountId() implements AddressStringFormat {
      @JsonProperty("type")
      public String type() {
        return "AccountId";
      }
    }

    final record Hex() implements AddressStringFormat {
      @JsonProperty("type")
      public String type() {
        return "Hex";
      }
    }

    final record Base64(Boolean url, Boolean test, Boolean bounce) implements AddressStringFormat {
      @JsonProperty("type")
      public String type() {
        return "Base64";
      }
    }
  }

  /**
   * @param compressed Must be encoded as base64. Compressed data.
   */
  public static final record ResultOfCompressZstd(String compressed) {
  }

  /**
   * @param address  Account address in any TON format.
   */
  public static final record ParamsOfGetAddressType(String address) {
  }
}
