import articulatedTruckIcon from '../assets/images/equipments/caminhao-articulado.png';
import offRoadTruckIcon from '../assets/images/equipments/caminhao-fora-de-estrada.png';
import waterTruckIcon from '../assets/images/equipments/caminhao-pipa.png';
import vacuumTruckIcon from '../assets/images/equipments/caminhao-vacuo.png';
import truckIcon from '../assets/images/equipments/caminhao.png';
import airCompressorIcon from '../assets/images/equipments/compressor-de-ar.png';
import defaultIcon from '../assets/images/equipments/default.png';
import forkliftIcon from '../assets/images/equipments/empilhadeira.png';
import excavatorIcon from '../assets/images/equipments/escavadeira.png';
import generatorIcon from '../assets/images/equipments/gerador.png';
import craneIcon from '../assets/images/equipments/guindaste.png';
import telehandlerIcon from '../assets/images/equipments/manipulador-telescopico.png';
import motorGraderIcon from '../assets/images/equipments/motoniveladora.png';
import wheelLoaderIcon from '../assets/images/equipments/pa-carregadeira.png';
import aerialPlatformIcon from '../assets/images/equipments/plataforma-elevatoria.png';
import backhoeIcon from '../assets/images/equipments/retroescavadeira.png';
import compactorIcon from '../assets/images/equipments/rolo-compactador.png';
import siloIcon from '../assets/images/equipments/silo.png';
import lightTowerIcon from '../assets/images/equipments/torre-de-iluminacao.png';
import crawlerTractorIcon from '../assets/images/equipments/trator-de-esteiras.png';
import tractorIcon from '../assets/images/equipments/trator.png';
import asphaltPaverIcon from '../assets/images/equipments/vibroacabadora.png';

const equipmentIcons = {
  'caminhão articulado': articulatedTruckIcon,
  'caminhao articulado': articulatedTruckIcon,

  'caminhão fora de estrada': offRoadTruckIcon,
  'caminhao fora de estrada': offRoadTruckIcon,

  'caminhão pipa': waterTruckIcon,
  'caminhao pipa': waterTruckIcon,

  'caminhão vácuo': vacuumTruckIcon,
  'caminhao vacuo': vacuumTruckIcon,

  'caminhão': truckIcon,
  'caminhao': truckIcon,

  'compressor de ar': airCompressorIcon,

  'empilhadeira': forkliftIcon,

  'escavadeira': excavatorIcon,

  'gerador': generatorIcon,

  'guindaste': craneIcon,

  'manipulador telescópico': telehandlerIcon,
  'manipulador telescopico': telehandlerIcon,

  'motoniveladora': motorGraderIcon,

  'pá carregadeira': wheelLoaderIcon,
  'pa carregadeira': wheelLoaderIcon,

  'plataforma elevatória': aerialPlatformIcon,
  'plataforma elevatoria': aerialPlatformIcon,

  'retroescavadeira': backhoeIcon,

  'rolo compactador': compactorIcon,

  'silo': siloIcon,

  'torre de iluminação': lightTowerIcon,
  'torre de iluminacao': lightTowerIcon,

  'trator de esteiras': crawlerTractorIcon,

  'trator': tractorIcon,

  'vibroacabadora': asphaltPaverIcon
};

export function getEquipmentIcon(category) {
  if (!category) {
    return defaultIcon;
  }

  const normalizedCategory = category
    .trim()
    .toLowerCase();

  return equipmentIcons[normalizedCategory] ?? defaultIcon;
}