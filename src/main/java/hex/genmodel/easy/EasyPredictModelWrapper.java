package hex.genmodel.easy;

import java.util.HashMap;

import hex.ModelCategory;
import hex.genmodel.GenModel;
import hex.genmodel.easy.exception.PredictException;
import hex.genmodel.easy.exception.PredictUnknownCategoricalLevelException;
import hex.genmodel.easy.exception.PredictUnknownTypeException;
import hex.genmodel.easy.exception.PredictWrongModelCategoryException;
import hex.genmodel.easy.prediction.AbstractPrediction;
import hex.genmodel.easy.prediction.AutoEncoderModelPrediction;
import hex.genmodel.easy.prediction.BinomialModelPrediction;
import hex.genmodel.easy.prediction.ClusteringModelPrediction;
import hex.genmodel.easy.prediction.MultinomialModelPrediction;
import hex.genmodel.easy.prediction.RegressionModelPrediction;

/**
 * An easy-to-use prediction wrapper for generated models.
 *
 * Note that for any given model, you must use the exact one correct predict method below based on the
 * model category.
 *
 * <p></p>
 * See the top-of-tree master version of this file <a href="https://github.com/h2oai/h2o-3/blob/master/h2o-genmodel/src/main/java/hex/genmodel/easy/EasyPredictModelWrapper.java" target="_blank">here on github</a>.
 */
public class EasyPredictModelWrapper implements java.io.Serializable {
  // All private members are read-only after the constructor.
  final private GenModel m;
  final private HashMap<String, Integer> modelColumnNameToIndexMap;
  final private HashMap<Integer, HashMap<String, Integer>> domainMap;

  /**
   * Create a wrapper for a generated model.
   * @param model The generated model
   */
  public EasyPredictModelWrapper(GenModel model) {
    m = model;

    // Create map of column names to index number.
    modelColumnNameToIndexMap = new HashMap<>();
    String[] modelColumnNames = m.getNames();
    for (int i = 0; i < modelColumnNames.length; i++) {
      modelColumnNameToIndexMap.put(modelColumnNames[i], i);
    }

    // Create map of input variable domain information.
    // This contains the categorical string to numeric mapping.
    domainMap = new HashMap<>();
    for (int i = 0; i < m.getNumCols(); i++) {
      String[] domainValues = m.getDomainValues(i);
      if (domainValues != null) {
        HashMap<String, Integer> m = new HashMap<>();
        for (int j = 0; j < domainValues.length; j++) {
          m.put(domainValues[j], j);
        }

        domainMap.put(i, m);
      }
    }
  }

  /**
   * Make a prediction on a new data point.
   *
   * The type of prediction returned depends on the model type.
   * The caller needs to know what type of prediction to expect.
   *
   * This call is convenient for generically automating model deployment.
   * For specific applications (where the kind of model is known and doesn't change), it is recommended to call
   * specific prediction calls like predictBinomial() directly.
   *
   * @param data A new data point.
   * @return The prediction.
   * @throws PredictException
   */
  public AbstractPrediction predict(RowData data) throws PredictException {
    switch (m.getModelCategory()) {
      case AutoEncoder:
        return predictAutoEncoder(data);
      case Binomial:
        return predictBinomial(data);
      case Multinomial:
        return predictMultinomial(data);
      case Clustering:
        return predictClustering(data);
      case Regression:
        return predictRegression(data);

      case Unknown:
        throw new PredictException("Unknown model category");
      default:
        throw new PredictException("Unhandled model category (" + m.getModelCategory() + ") in switch statement");
    }
  }

  /**
   * Make a prediction on a new data point using an AutoEncoder model.
   *
   * @param data A new data point.
   * @return The prediction.
   * @throws PredictException
   */
  public AutoEncoderModelPrediction predictAutoEncoder(RowData data) throws PredictException {
    double[] preds = preamble(ModelCategory.AutoEncoder, data);
    throw new RuntimeException("Unimplemented " + preds.length);
  }

  /**
   * Make a prediction on a new data point using a Binomial model.
   *
   * @param data A new data point.
   * @return The prediction.
   * @throws PredictException
   */
  public BinomialModelPrediction predictBinomial(RowData data) throws PredictException {
    double[] preds = preamble(ModelCategory.Binomial, data);

    BinomialModelPrediction p = new BinomialModelPrediction();
    p.classProbabilities = new double[m.getNumResponseClasses()];
    double d = preds[0];
    p.labelIndex = (int) d;
    String[] domainValues = m.getDomainValues(m.getResponseIdx());
    p.label = domainValues[p.labelIndex];
    System.arraycopy(preds, 1, p.classProbabilities, 0, p.classProbabilities.length);

    return p;
  }

  /**
   * Make a prediction on a new data point using a Multinomial model.
   *
   * @param data A new data point.
   * @return The prediction.
   * @throws PredictException
   */
  public MultinomialModelPrediction predictMultinomial(RowData data) throws PredictException {
    double[] preds = preamble(ModelCategory.Multinomial, data);

    MultinomialModelPrediction p = new MultinomialModelPrediction();
    p.classProbabilities = new double[m.getNumResponseClasses()];
    p.labelIndex = (int) preds[0];
    String[] domainValues = m.getDomainValues(m.getResponseIdx());
    p.label = domainValues[p.labelIndex];
    System.arraycopy(preds, 1, p.classProbabilities, 0, p.classProbabilities.length);

    return p;
  }

  /**
   * Make a prediction on a new data point using a Clustering model.
   *
   * @param data A new data point.
   * @return The prediction.
   * @throws PredictException
   */
  public ClusteringModelPrediction predictClustering(RowData data) throws PredictException {
    double[] preds = preamble(ModelCategory.Clustering, data);

    ClusteringModelPrediction p = new ClusteringModelPrediction();
    p.cluster = (int) preds[0];

    return p;
  }

  /**
   * Make a prediction on a new data point using a Regression model.
   *
   * @param data A new data point.
   * @return The prediction.
   * @throws PredictException
   */
  public RegressionModelPrediction predictRegression(RowData data) throws PredictException {
    double[] preds = preamble(ModelCategory.Regression, data);

    RegressionModelPrediction p = new RegressionModelPrediction();
    p.value = preds[0];

    return p;
  }

  //----------------------------------------------------------------------
  // Transparent methods passed through to GenModel.
  //----------------------------------------------------------------------

  /**
   * Get the category (type) of model.
   * @return The category.
   */
  public ModelCategory getModelCategory() {
    return m.getModelCategory();
  }

  /**
   * Get the array of levels for the response column.
   * "Domain" just means list of level names for a categorical (aka factor, enum) column.
   * If the response column is numerical and not categorical, this will return null.
   *
   * @return The array.
   */
  public String[] getResponseDomainValues() {
    return m.getDomainValues(m.getResponseIdx());
  }

  /**
   * Some autoencoder thing, I'm not sure what this does.
   * @return CSV header for autoencoder.
   */
  public String getHeader() {
    return m.getHeader();
  }

  //----------------------------------------------------------------------
  // Private methods below this line.
  //----------------------------------------------------------------------

  private void validateModelCategory(ModelCategory c) throws PredictException {
    if (m.getModelCategory() != c) {
      throw new PredictWrongModelCategoryException("Prediction type unsupported by model of category " + m.getModelCategory());
    }
  }

  private double[] preamble(ModelCategory c, RowData data) throws PredictException {
    validateModelCategory(c);
    double[] preds = new double[m.getPredsSize()];
    preds = predict(data, preds);
    return preds;
  }

  private void setToNaN(double[] arr) {
    for (int i = 0; i < arr.length; i++) {
      arr[i] = Double.NaN;
    }
  }

  private void fillRawData(RowData data, double[] rawData) throws PredictException {
    for (String dataColumnName : data.keySet()) {
      Integer index = modelColumnNameToIndexMap.get(dataColumnName);

      // Skip column names that are not known.
      if (index == null) {
        continue;
      }

      String[] domainValues = m.getDomainValues(index);
      if (domainValues == null) {
        // Column has numeric value.
        double value;
        Object o = data.get(dataColumnName);
        if (o instanceof String) {
          String s = (String) o;
          value = Double.parseDouble(s);
        }
        else if (o instanceof Double) {
          value = (Double) o;
        }
        else {
          throw new PredictUnknownTypeException("Unknown object type " + o.getClass().getName());
        }

        rawData[index] = value;
      }
      else {
        // Column has categorical value.
        Object o = data.get(dataColumnName);
        if (o instanceof String) {
          String levelName = (String) o;
          HashMap<String, Integer> columnDomainMap = domainMap.get(index);
          Integer levelIndex = columnDomainMap.get(levelName);
          if (levelIndex == null) {
            throw new PredictUnknownCategoricalLevelException("Unknown categorical level (" + dataColumnName + "," + levelName + ")", dataColumnName, levelName);
          }
          double value = levelIndex;

          rawData[index] = value;
        }
        else {
          throw new PredictUnknownTypeException("Unknown object type " + o.getClass().getName());
        }
      }
    }
  }

  private double[] predict(RowData data, double[] preds) throws PredictException {
    double[] rawData = new double[m.nfeatures()];
    setToNaN(rawData);
    fillRawData(data, rawData);
    preds = m.score0(rawData, preds);
    return preds;
  }
}
